# Documentazione Tecnica: Funzionalità "Indovina Sketch"

## 1. Panoramica Funzionale
La funzionalità **Indovina Sketch** consente agli utenti autenticati di provare a indovinare la parola segreta associata ai disegni realizzati dagli altri utenti della community.

### Regole di Gioco e Anti-Cheating
* **Regola One-Shot per Sketch:** ogni utente può giocare ciascuno sketch una sola volta, con un massimo di **10 tentativi** a disposizione e **nessun limite di tempo**.
* **Normalizzazione dell'Input:** la parola inserita dal giocatore viene ripulita dagli spazi superflui (`trim()`) e convertita in minuscolo (`toLowerCase()`), rendendo il confronto insensibile a maiuscole, minuscole o caratteri misti.
* **Protezione della Parola Segreta:** la parola da indovinare non viene mai inviata al client durante la partita; la verifica avviene esclusivamente lato server e la soluzione viene rivelata solo a partita conclusa (vittoria, esaurimento dei 10 tentativi o resa).
* **Persistenza e Recupero delle Partite in Sospeso:** la sessione viene registrata su database fin dal momento dell'apertura dello sketch. Se il giocatore ricarica la pagina o chiude il browser prima di terminare, i tentativi già consumati restano salvati e la partita può essere ripresa dalla Homepage senza perdere né azzerare i tentativi rimasti.
* **Resa Esplicita ("Arrenditi"):** il giocatore può abbandonare in qualsiasi momento una partita in corso confermando la resa; l'incontro viene immediatamente chiuso come sconfitta e la parola segreta viene rivelata.

---

## 2. Architettura Backend (Spring Boot + MongoDB)

### 2.1 Aggiornamento dei Modelli Dati
* **Entità `Guess` (Collection `guesses`):**
  * Vincolo di unicità `@CompoundIndex(name = "user_sketch_unique", def = "{'username': 1, 'sketchId': 1}", unique = true)` per impedire partite duplicate sullo stesso sketch da parte dello stesso giocatore.
  * Campi principali: `id`, `sketchId`, `username`, `attemptsUsed` (contatore tentativi effettuati), `attemptedWords` (storico parole già provate), `finished` (booleano che distingue le partite in sospeso da quelle concluse), `correct` (esito finale), `startedAt` e `finishedAt`.
* **Entità `Sketch` (Collection `sketches`):**
  * Aggiunto il campo `playedByUsernames` (`List<String>`) per tracciare tutti gli utenti che hanno completato una partita su quel disegno.
* **Entità `User` (Collection `users`):**
  * Aggiornamento atomico dei contatori statistici utilizzati per il profilo e per le classifiche: `wordsGuessed`, `wordsFailed`, `totalGuessAttemptsUsed`, `sketchesPlayedByOthers`, `sketchesGuessedByOthers` e `sketchesCreated`.

### 2.2 Logica di Business (`GuessServiceImpl`)
1. **Avvio o Ripristino Sessione (`startOrResumeGuess`):**
   * Verifica che lo sketch esista, sia completato (`completed == true`) e non appartenga al giocatore stesso.
   * Se esiste già un documento `Guess` per `(username, sketchId)`:
     * Se `finished == true`, blocca l'accesso (`IllegalStateException`).
     * Se `finished == false`, restituisce lo stato salvato (`attemptsUsed`, `attemptsRemaining`, `attemptedWords`), consentendo il ripristino esatto della partita.
   * Altrimenti crea e salva un nuovo documento `Guess` con `attemptsUsed = 0` e `finished = false`.

2. **Invio Tentativo (`submitAttempt`):**
   * Incrementa `attemptsUsed` e aggiunge la parola normalizzata ad `attemptedWords`.
   * Confronta `rawWord.trim().toLowerCase()` con `sketch.getWord().trim().toLowerCase()`:
     * **Esito Positivo (`WON`):** chiude il `Guess` (`finished = true`, `correct = true`), incrementa `wordsGuessed` e `totalGuessAttemptsUsed` del giocatore, incrementa `sketchesPlayedByOthers` e `sketchesGuessedByOthers` dell'autore, aggiunge lo username in `sketch.playedByUsernames` e rivela `secretWord`.
     * **Esito Negativo al 10° Tentativo (`LOST`):** chiude il `Guess` (`finished = true`, `correct = false`), incrementa `wordsFailed` e `totalGuessAttemptsUsed` del giocatore, incrementa `sketchesPlayedByOthers` dell'autore, registra lo username nello sketch e rivela `secretWord`.
     * **Esito Negativo con Tentativi Residui (`IN_PROGRESS`):** salva il progresso mantenendo `finished = false` e `secretWord = null`.

3. **Resa del Giocatore (`surrenderGuess`):**
   * Imposta `finished = true` e `correct = false` sul `Guess` attivo, applica le statistiche di sconfitta (conteggiando i tentativi effettivamente consumati fino a quel momento) e restituisce la parola segreta.

4. **Recupero Partite in Sospeso (`getInProgressGuesses`):**
   * Estrae tutti i documenti `Guess` dell'utente con `finished = false` (`findByUsernameAndFinishedFalseOrderByStartedAtDesc`) e li arricchisce con i metadati dello sketch corrispondente (`InProgressGuessResponse`).

### 2.3 Endpoint REST (`GuessController`)
Tutti gli endpoint richiedono autenticazione JWT tramite cookie `HttpOnly` (`quicksketch_jwt`):
* `POST /api/guesses/start/{sketchId}` — Avvia una nuova partita o recupera una partita già in sospeso.
* `POST /api/guesses/{sketchId}/attempt` — Invia una parola da verificare (`GuessAttemptRequest`).
* `POST /api/guesses/{sketchId}/surrender` — Chiude la partita corrente come persa per resa.
* `GET /api/guesses/in-progress` — Restituisce l'elenco delle partite lasciate in sospeso dall'utente.

---

## 3. Architettura Frontend (Angular)

### 3.1 Servizio HTTP (`GuessService`)
* Centralizza le comunicazioni verso `/api/guesses/**` configurando `{ withCredentials: true }` su tutte le chiamate per garantire l'invio automatico del cookie di sessione.

### 3.2 Integrazione nella Homepage (`HomeComponent`)
* Quando l'utente è autenticato, `HomeComponent` carica in parallelo (`forkJoin`) sia gli sketch nuovi giocabili (`getPlayableSketches()`) sia le eventuali partite lasciate in sospeso (`getInProgressGuesses()`).
* Se sono presenti partite non concluse, viene mostrata in evidenza la sezione **"Partite in sospeso"** con il badge dei tentativi già usati (`attemptsUsed / 10`) e il pulsante **"Riprendi partita"** collegato a `/guess/:id`.

### 3.3 Schermata di Gioco (`GuessSketchComponent`)
* **Gestione del Ciclo di Vita:** all'apertura della rotta `/guess/:id`, invoca `startOrResumeGuess(id)` per inizializzare o ripristinare la sessione.
* **Controlli e Feedback:**
  * Mostra l'immagine dello sketch, l'autore e il contatore dinamico dei tentativi (`attemptsUsed / 10`).
  * Impedisce lato client l'invio duplicato di parole già presenti in `attemptedWords`, mostrando un avviso senza consumare un tentativo.
  * Visualizza sotto il form i chip delle parole già provate.
  * Include il flusso di resa a due passaggi (*"Arrenditi"* $\rightarrow$ *"Sì, arrenditi"*) per evitare click accidentali.
  * A partita terminata (`WON` o `LOST`), nasconde i controlli di input e mostra il riquadro riepilogativo con la parola segreta rivelata e il collegamento per tornare alla Homepage.
