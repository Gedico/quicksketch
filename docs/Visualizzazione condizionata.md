# Documentazione Tecnica: Visualizzazione Condizionata degli Sketch

## 1. Obiettivo Funzionale
La piattaforma **QuickSketch** adatta dinamicamente il catalogo degli sketch visualizzati nella Homepage in base allo stato di autenticazione dell'utente:
* **Utente Non Autenticato (Visitatore):** accede alla galleria pubblica in sola lettura (`GET /api/sketches`), visualizzando tutti gli sketch completati dalla community ordinati dal più recente.
* **Utente Autenticato (Giocatore):** accede esclusivamente agli sketch giocabili (`GET /api/sketches/playable`), ovvero i disegni completati da **altri utenti** sui quali non ha ancora effettuato alcun tentativo di risposta (né indovinati né sbagliati).

---

## 2. Architettura Backend (Spring Boot + MongoDB)

### 2.1 Tracciamento dei Tentativi (`Guess` & `GuessRepository`)
* **Collection MongoDB:** `guesses`
* **Vincolo di Unicità:** definito tramite `@CompoundIndex(name = "user_sketch_unique", def = "{'username': 1, 'sketchId': 1}", unique = true)` per garantire a livello di database la regola *one-shot* (un solo tentativo per utente su ogni singolo sketch).
* **Campi principali:** `id`, `sketchId`, `username`, `guessedWord`, `correct` (booleano), `attemptedAt` (`LocalDateTime`).
* **Query Repository:** `GuessRepository.findByUsername(String username)` estrae tutti i tentativi già effettuati dall'utente corrente.

### 2.2 Filtraggio degli Sketch Giocabili (`SketchRepository` & `SketchServiceImpl`)
* **Query MongoDB derivata:**
  ```java
  findByCompletedTrueAndAuthorUsernameNotAndIdNotInOrderByCreatedAtDesc(
      String authorUsername,
      Collection<String> excludedIds
  )
