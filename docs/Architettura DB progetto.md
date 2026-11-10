# Architettura Funzionale del Database e Scelte di Design

Questo documento sintetizza la struttura concettuale del database MongoDB (`quicksketch_db`), definendo la responsabilità di ogni collezione e le decisioni prese per ottimizzare l'applicazione.

---

## 1. Utente (`User` -> collezione `users`)

### Cosa rappresenta
Rappresenta l'identità dell'utente registrato. Funge sia da profilo di autenticazione (credenziali) sia da storico aggregato delle sue prestazioni all'interno della piattaforma.

### Scelte Architetturali e di Design
- **Eliminazione dei Ruoli (`roles`):** L'applicazione prevede una distinzione netta ma binaria (Utente Anonimo vs Utente Autenticato). Non esistendo pannelli amministrativi o permessi multilivello, la gestione dei ruoli (es. `ROLE_ADMIN`) è stata omessa per evitare sovrastrutture inutili.
- **Appiattimento delle Statistiche (`UserStats`):** Invece di creare un sotto-documento annidato per le metriche, le statistiche (disegni creati, parole indovinate, tentativi totali usati, ecc.) sono state integrate come campi diretti del documento `User`. Questo approccio semplifica l'aggiornamento dei dati e velocizza le query per la generazione delle classifiche.
- **Immutabilità della registrazione:** La data di creazione (`createdAt`) non prevede metodi di modifica per garantire l'integrità temporale del profilo.

---

## 2. Disegno (`Sketch` -> collezione `sketches`)

### Cosa rappresenta
Rappresenta la tela/disegno prodotta da un utente durante la fase di creazione del gioco, associata alla parola che doveva rappresentare.

### Scelte Architetturali e di Design
- **Salvataggio dell'Immagine in Base64:** Il tratto grafico generato dal Canvas HTML5 viene serializzato come stringa Base64 direttamente nel campo `imageData`. Questo evita l'uso di file storage esterni (Amazon S3 o file system locale), rendendo il database del tutto autonomo nel servire le immagini alla galleria e alla schermata di gioco.
- **Denormalizzazione dell'Autore (`authorUsername`):** Viene memorizzato direttamente l'username unico dell'autore anziché il solo ID di riferimento. Nei database NoSQL come MongoDB, questa scelta elimina la necessità di eseguire JOIN o lookup complesse per mostrare il nome del creatore sotto ogni disegno nella galleria.

---

## 3. Sessione di Gioco e Tentativi (`GuessAttempt` -> collezione `guess_attempts`)

### Cosa rappresenta
È la collezione centrale per la gestione delle partite. Traccia lo stato di avanzamento di uno specifico utente mentre prova a indovinare uno specifico disegno.

### Scelte Architetturali e di Design
- **Persistenza e Regola dei 10 Tentativi:** Salva ogni singolo tentativo digitato dall'utente. Questo garantisce che se l'utente ricarica la pagina (F5), chiude il browser o cambia dispositivo, lo stato del gioco viene recuperato esattamente da dove era stato lasciato.
- **Indice Composto Univoco (`username` + `sketchId`):** È stata definita una chiave composta univoca a livello di database sulla coppia `(username, sketchId)`. Questa scelta impedisce l'avvio di sessioni duplicate, bloccando alla radice tentativi di frode o raggiri della classifica.
- **Gestione dello Stato (`isGuessed` e `isCompleted`):** Permette di bloccare gli input dell'interfaccia quando la partita termina, sia in caso di vittoria (parola indovinata) sia in caso di sconfitta (10 tentativi esauriti).

---

## 4. Dizionario Parole (`WordPool` -> collezione `words`)

### Cosa rappresenta
Rappresenta il serbatoio/pool di parole valide da cui il sistema estrae in modo casuale il soggetto che l'utente dovrà disegnare.

### Scelte Architetturali e di Design
- **Univocità della Parola:** Il campo `word` presenta un indice unico per garantire che non vengano inseriti duplicati all'interno del catalogo.
- **Inizializzazione Automatica:** La popolazione iniziale della collezione è delegata al componente backend `DatabaseInitializer`, che controlla all'avvio lo stato della collezione ed esegue il caricamento iniziale solo se il DB risulta vuoto.
