# Documentazione Tecnica: Classifica Globale e Statistiche Personali

## 1. Panoramica Funzionale
Le funzionalità di **Classifica Globale** e **Statistiche Personali** completano l'ecosistema competitivo di **QuickSketch**, valorizzando sia l'abilità nel disegno (ruolo *Sketcher*) sia la capacità di intuizione ed efficienza nei tentativi (ruolo *Guesser*).

### Regole di Accesso e Visibilità
* **Classifica Globale (`/leaderboard`):** accessibile pubblicamente a tutti gli utenti, compresi i visitatori non autenticati. È suddivisa in due graduatorie distinte navigabili tramite tab: **Migliori Guessers** e **Migliori Sketchers**.
* **Statistiche Personali (`/profile`):** riservata agli utenti autenticati, mostra in tempo reale il riepilogo completo delle prestazioni individuali, i punteggi Elo, il posizionamento nelle due classifiche globali e le metriche aggregate di gioco.

---

## 2. Modello Matematico di Ranking (Rating Elo Pesato)
Per evitare distorsioni dovute a campioni statistici troppo ridotti (ad esempio un utente con 1 sola partita vinta al 100% che supera chi ha 90 vittorie su 100) o a vittorie ottenute consumando un numero eccessivo di tentativi, entrambe le classifiche utilizzano una **media pesata bayesiana (base 1000)** con prior statistico $C = 5$ partite al $50\%$ di successo.

### 2.1 Sketcher Elo (Classifica Disegnatori)
Valuta la comprensibilità dei disegni creati dall'utente in rapporto al numero di partite giocate dagli altri utenti:
$$\text{SuccessRate}_{\text{bayes}} = \frac{\text{sketchesGuessedByOthers} + (5 \times 0.5)}{\text{sketchesPlayedByOthers} + 5}$$
$$\text{SketcherElo} = \text{round}\Big(1000 + (\text{SuccessRate}_{\text{bayes}} - 0.5) \times 1000 + \min(\text{sketchesCreated} \times 5, 150)\Big)$$
* Espone inoltre la percentuale grezza di sketch indovinati (`guessedPercentage`) come richiesto dalle specifiche funzionali.

### 2.2 Guesser Elo (Classifica Indovinatori)
Bilancia il volume di parole indovinate (`wordsGuessed`), il tasso di vittoria bayesiano e l'**efficienza nei tentativi** (considerando il limite massimo di 10 tentativi per partita):
$$\text{WinRate}_{\text{bayes}} = \frac{\text{wordsGuessed} + (5 \times 0.5)}{\text{wordsGuessed} + \text{wordsFailed} + 5}$$
$$\text{Efficiency} = \max\left(0, \; 1 - \frac{\text{totalGuessAttemptsUsed}}{(\text{wordsGuessed} + \text{wordsFailed} + 1) \times 10}\right)$$
$$\text{GuesserElo} = \text{round}\Big(1000 + (\text{WinRate}_{\text{bayes}} - 0.5) \times 800 + \text{Efficiency} \times 200 + (\text{wordsGuessed} \times 10)\Big)$$
* Gli utenti con `0` partite giocate partono dal punteggio base neutro di **1000 Elo**.

---

## 3. Architettura Backend (Spring Boot + MongoDB)

### 3.1 DTO di Risposta (`dto/stats/`)
* **`SketcherLeaderboardEntry`:** rappresenta una riga della classifica disegnatori (`rank`, `username`, `sketcherElo`, `sketchesCreated`, `sketchesPlayedByOthers`, `sketchesGuessedByOthers`, `guessedPercentage`).
* **`GuesserLeaderboardEntry`:** rappresenta una riga della classifica indovinatori (`rank`, `username`, `guesserElo`, `wordsGuessed`, `wordsFailed`, `totalGuessAttemptsUsed`, `winRatePercentage`).
* **`GlobalLeaderboardsResponse`:** incapsula entrambe le liste (`topSketchers` e `topGuessers`) in un'unica risposta REST.
* **`UserProfileStatsResponse`:** aggrega dati anagrafici (`username`, `email`, `createdAt`), Elo e rank nelle due graduatorie, metriche da disegnatore (`sketchesCreated`, `sketchesPlayedByOthers`, `sketchesGuessedByOthers`, `sketchGuessedPercentage`) e metriche da indovinatore (`wordsGuessed`, `wordsFailed`, `totalGuessAttemptsUsed`, `winRatePercentage`, `averageAttemptsPerGame`).

### 3.2 Logica di Calcolo (`StatsServiceImpl`)
* Recupera i contatori atomici già persistiti sui documenti della collection `users`.
* Ordina in modo deterministico entrambe le graduatorie per Elo decrescente con criteri secondari di spareggio (`wordsGuessed` / `sketchesGuessedByOthers`, `totalGuessAttemptsUsed` crescente, `username`).
* Calcola il posizionamento (`rank`) dell'utente autenticato all'interno di entrambe le classifiche per esporlo nella vista profilo.

### 3.3 Endpoint REST e Sincronizzazione Sessione
* `GET /api/leaderboard/global` (`LeaderboardController`) — Endpoint pubblico (`permitAll()`) che restituisce entrambe le classifiche globali.
* `GET /api/users/me/stats` (`UserController`) — Endpoint protetto da cookie JWT `HttpOnly` che restituisce le statistiche personali dell'utente autenticato.
* `GET /api/auth/me` (`AuthController`) — Endpoint di verifica sessione utilizzato dal frontend al caricamento/refresh dell'applicazione per sincronizzare lo stato di autenticazione in memoria con il cookie `HttpOnly`.

---

## 4. Architettura Frontend (Angular)

### 4.1 Servizi e Sincronizzazione Stato (`StatsService`, `AuthService`, `credentialsInterceptor`)
* **`StatsService`:** gestisce le chiamate HTTP verso `/api/leaderboard/global` e `/api/users/me/stats` (con `withCredentials: true`).
* **`AuthService.checkSession()`:** invoca `GET /api/auth/me` all'avvio dell'applicazione per mantenere sincronizzato `isAuthenticatedSignal` dopo un refresh del browser (`F5`).
* **`credentialsInterceptor`:** allega automaticamente i cookie `HttpOnly` a tutte le richieste e riallinea `isAuthenticatedSignal` a `false` qualora una chiamata protetta riceva `401 Unauthorized` o `403 Forbidden`.

### 4.2 Componenti UI
* **`LeaderboardComponent` (`/leaderboard`):** interfaccia a schede per alternare la visualizzazione tra **Migliori Guessers** e **Migliori Sketchers**, evidenziando il podio (1°, 2°, 3° posto), gli Elo e le percentuali di successo.
* **`ProfileComponent` (`/profile`):** dashboard personale organizzata in banner utente (con badge Elo e rank) e due griglie statistiche dedicate alle prestazioni da **Guesser** e da **Sketcher**.
* **`NavbarComponent`:** aggiornata con il link pubblico **Classifica** e il link **Statistiche** condizionato allo stato di autenticazione (`isAuthenticatedSignal()`).
