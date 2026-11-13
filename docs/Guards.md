# Documentazione Tecnica: Protezione delle Rotte (Route Guards) e Sincronizzazione Sessione

## 1. Obiettivo e Architettura di Sicurezza
Per garantire un'esperienza utente coerente e impedire accessi non autorizzati alle viste applicative lato client, **QuickSketch** implementa un sistema di **Functional Route Guards** (`CanActivateFn`) integrato con lo stato reattivo basato su Angular Signals e con l'autenticazione stateless via cookie `HttpOnly`.

La protezione opera su due livelli complementari:
1. **Livello Client (Angular Router Guards):** intercetta la navigazione prima dell'attivazione dei componenti e reindirizza l'utente in base allo stato reale della sessione.
2. **Livello Server (Spring Security):** valida il cookie JWT `HttpOnly` ad ogni richiesta REST, garantendo che gli endpoint protetti non siano mai accessibili nemmeno bypassando il frontend.

---

## 2. Sincronizzazione della Sessione al Refresh (`ensureAuthenticated`)
Poiché il token JWT è custodito in un cookie `HttpOnly` (non leggibile da JavaScript per protezione Anti-XSS), lo stato di autenticazione in Angular (`isAuthenticatedSignal`) risiede esclusivamente in memoria.

Al caricamento iniziale dell'applicazione o in seguito a un aggiornamento manuale della pagina (`F5`), `AuthService` esegue una verifica asincrona tramite il metodo `ensureAuthenticated()`:
* Se `isAuthenticatedSignal()` è già `true`, restituisce immediatamente `of(true)` evitando chiamate HTTP superflue.
* Se `isAuthenticatedSignal()` è `false` (ad esempio dopo un refresh del browser), interroga l'endpoint `GET /api/auth/me` (`withCredentials: true`):
  * **Risposta `200 OK`:** il cookie JWT è ancora valido; il signal viene impostato a `true` e la navigazione prosegue senza interruzioni.
  * **Risposta `401 Unauthorized` / `403 Forbidden`:** nessuna sessione attiva; il signal rimane `false`.

---

## 3. Implementazione delle Guards (`src/app/core/guards/`)

### 3.1 `authGuard` (Protezione Rotte Riservate)
Impedisce agli utenti non autenticati di accedere alle funzionalità riservate dell'applicazione.
* **Rotte protette:**
  * `/create` (`CreateSketchComponent`)
  * `/guess/:id` (`GuessSketchComponent`)
  * `/profile` (`ProfileComponent`)
* **Comportamento:** se `ensureAuthenticated()` restituisce `false`, blocca l'attivazione della rotta e reindirizza a `/login` allegando il parametro `returnUrl` con l'URL originariamente richiesto.

### 3.2 `guestGuard` (Protezione Rotte Pubbliche di Accesso)
Impedisce agli utenti **già autenticati** di tornare sulle schermate di autenticazione o registrazione.
* **Rotte protette:**
  * `/login` (`LoginComponent`)
  * `/register` (`RegisterComponent`)
* **Comportamento:** se `ensureAuthenticated()` restituisce `true`, blocca l'accesso e reindirizza automaticamente alla Home (`/`).

---

## 4. Configurazione del Routing (`app.routes.ts`)
Le guard sono collegate dichiarativamente alle rispettive definizioni di rotta tramite la proprietà `canActivate`:
* **Rotte libere:** `/` (Home) e `/leaderboard` (Classifica Globale) restano accessibili a tutti gli utenti, inclusi i visitatori non autenticati.
* **Rotte con `guestGuard`:** `/login`, `/register`.
* **Rotte con `authGuard`:** `/create`, `/guess/:id`, `/profile`.
* **Fallback (`**`):** qualsiasi percorso non riconosciuto viene reindirizzato alla Home (`/`).
