# Documentazione Tecnica: Funzionalità "Crea Sketch"

## 1. Panoramica Funzionale
La funzionalità **Crea Sketch** permette agli utenti autenticati di realizzare un disegno a mano libera su una tela digitale partendo da una parola casuale proposta dal sistema, entro un tempo limite prestabilito. Al termine del timer (o su invio manuale dell'utente), il disegno viene salvato automaticamente sul database e reso disponibile nella galleria pubblica e nelle sessioni di gioco degli altri utenti.

---

## 2. Architettura Backend (Spring Boot + MongoDB)

### 2.1 Modello Dati (`Sketch`)
* **Collection MongoDB:** `sketches`
* **Campi principali:**
  * `id`: identificativo univoco generato da MongoDB.
  * `authorUsername`: username dell'utente autenticato che ha avviato la sessione.
  * `word`: parola segreta assegnata al disegno (normalizzata in minuscolo).
  * `imageData`: rappresentazione grafica del canvas codificata in formato **Base64 Data URL** (`image/png`).
  * `completed`: flag booleano (`false` all'avvio della sessione, `true` al completamento/salvataggio finale).
  * `createdAt`: timestamp di creazione (`Instant`).

### 2.2 Gestione del Dizionario Parole (`WordService` & `WordServiceImpl`)
* All'avvio dell'applicazione (`@PostConstruct`), `WordServiceImpl` legge il file `src/main/resources/words.txt` tramite `ClassPathResource`.
* Le parole vengono estratte, ripulite dagli spazi (`trim()`), convertite in minuscolo e caricate in memoria senza duplicati.
* Il metodo `getRandomWords(int count)` mescola una copia della lista tramite `Collections.shuffle` e restituisce una sotto-lista di **3 parole casuali distinte**, con una lista di fallback interna in caso di assenza del file.

### 2.3 Flusso Transazionale a Due Fasi (`SketchServiceImpl` & `SketchController`)
Per garantire integrità ed evitare manipolazioni lato client, la creazione dello sketch è divisa in due endpoint protetti da autenticazione JWT tramite cookie `HttpOnly` (`quicksketch_jwt`):

1. **Avvio Sessione — `POST /api/sketches/start`**
   * **Input:** `StartSketchRequest` contenente la parola scelta dall'utente.
   * **Logica:** Crea su MongoDB una bozza di `Sketch` con `authorUsername` estratto dal `SecurityContext`, la parola scelta e `completed = false`.
   * **Output:** `SketchResponse` (mappato tramite `SketchMapper`) contenente l'`id` della sessione appena creata.

2. **Completamento e Salvataggio — `PUT /api/sketches/{id}/complete`**
   * **Input:** `CompleteSketchRequest` contenente `imageData` (stringa Base64 del canvas).
   * **Controlli di Sicurezza e Integrità:**
     * Verifica dell'esistenza dello sketch su database.
     * Controllo di ownership: solo l'utente con `authorUsername` corrispondente al creatore può completare lo sketch (`SecurityException` in caso contrario).
     * Controllo di idempotenza: impedisce il ri-salvataggio di uno sketch già marcato come `completed = true` (`IllegalStateException`).
   * **Output:** Aggiorna `imageData`, imposta `completed = true` e restituisce `SketchResponse`.

3. **Fornitura Parole Casuali — `GET /api/words/random?count=3`**
   * Restituisce un array JSON di 3 parole estratte dal dizionario per alimentare la modale di selezione iniziale.

---

## 3. Architettura Frontend (Angular)

### 3.1 Autenticazione e Sicurezza delle Chiamate (`AuthService` & `SketchService`)
* Le chiamate HTTP verso gli endpoint protetti (`/api/sketches/start`, `/api/sketches/{id}/complete`, `/api/words/random`) includono l'opzione `{ withCredentials: true }`, consentendo al browser di allegare automaticamente il cookie `HttpOnly` impostato in fase di login.

### 3.2 Flusso Utente e Gestione del Canvas (`CreateSketchComponent`)
1. **Selezione della Parola (Modale Iniziale):**
   * All'ingresso nella pagina di creazione, viene mostrata una modale che carica 3 parole casuali dal backend.
   * L'utente può richiedere un nuovo set di 3 parole (*"Altre 3 parole"*) oppure selezionarne una per avviare la partita.
   * Al click sulla parola, il frontend invia `POST /api/sketches/start`, memorizza lo `sketchId` restituito e avvia la sessione di disegno.

2. **Area di Disegno HTML5 `<canvas>`:**
   * Implementazione nativa tramite API `CanvasRenderingContext2D` con supporto sia agli eventi **mouse** (`mousedown`, `mousemove`, `mouseup`, `mouseleave`) sia agli eventi **touch** per dispositivi mobili.
   * Strumenti disponibili:
     * **Pennello:** selezione colore (palette rapida + color picker) e regolazione dello spessore del tratto.
     * **Gomma:** cancellazione selettiva tramite tratto bianco.
     * **Pulisci tutto:** reset completo della tela su sfondo bianco.

3. **Timer Countdown e Salvataggio Automatico (Auto-Save):**
   * All'avvio del disegno parte un timer a scalare (`setInterval`).
   * Allo scadere del tempo (`0 secondi`) — oppure al click esplicito sul pulsante di invio anticipato — il componente converte il contenuto del canvas in immagine PNG tramite `canvas.toDataURL('image/png')`.
   * Il payload viene inviato automaticamente a `PUT /api/sketches/{sketchId}/complete` e, a salvataggio confermato, l'utente viene reindirizzato alla Homepage dove lo sketch appena creato risulta immediatamente visibile.
