# QuickSketch  Sicurezza e Autenticazione

## 1. Overview
QuickSketch adotta un'architettura di autenticazione stateless basata su **JSON Web Token (JWT)** allineata alle direttive **OWASP**.

Per prevenire attacchi di tipo **Cross-Site Scripting (XSS)**, l'applicazione evita la memorizzazione dei token nel client via `localStorage` o `sessionStorage`, trasmettendoli esclusivamente tramite **Cookie HttpOnly e SameSite**.

---

## 2. Decisioni Architetturali

### A. Protezione Anti-XSS (Cookie HttpOnly)
* **Vulnerabilità:** I token memorizzati in `localStorage` sono accessibili da qualunque script JS sul dominio. In presenza di una falla XSS, l'attaccante può sottrarre il token.
* **Soluzione:** Il backend Spring Boot restituisce il JWT racchiuso in un cookie con la direttiva `HttpOnly`. Il browser gestisce automaticamente il cookie nelle richieste HTTP, ma il codice JavaScript client non può leggerlo o estrarlo (`document.cookie` non ne mostra il contenuto).

### B. Protezione Anti-CSRF (SameSite & CORS)
* **Soluzione:** I cookie sono configurati con il parametro `SameSite=Lax`. Sul backend Spring Security, la configurazione CORS definisce in modo esplicito l'origine consentita (`http://localhost:4200`) e abilita `allowCredentials=true`.

### C. Esternalizzazione delle Credenziali e Variabili d'Ambiente
* **Soluzione:** La chiave di cifratura `JWT_SECRET` e le stringhe di connessione al database sono esternalizzate nel file `.env` ed escluse dal tracciamento di versione via `.gitignore`. Viene creata una copia di esempio `.env.example` per il repository.

### D. Hashing delle Password
* Le credenziali dell'utente vengono salvate nel database MongoDB cifrando la password mediante l'algoritmo **BCrypt** con fattore di costo 10.
