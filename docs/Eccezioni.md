# QuickSketch — Architettura della Gestione Eccezioni (Backend)

Questo documento descrive la gerarchia delle eccezioni personalizzate e il meccanismo di gestione centralizzata degli errori implementati nel backend Spring Boot di **QuickSketch**.

---

## 1. Principi Architetturali

* **Separazione delle responsabilità:** I `Controller` non contengono blocchi `try/catch`; si limitano a estrarre l'identità dell'utente autenticato, delegare la logica ai `Service` e restituire risposte tipizzate (`ResponseEntity<T>`).
* **Eccezioni di dominio esplicite:** I `Service` non lanciano eccezioni generiche (`RuntimeException`, `IllegalArgumentException`, `IllegalStateException`, `SecurityException`), ma eccezioni custom suddivise per dominio applicativo.
* **Gestione centralizzata (`@RestControllerAdvice`):** La classe `GlobalExceptionHandler` intercetta tutte le eccezioni di dominio e di validazione (`@Valid`), traducendole in codici di stato HTTP semantici e in un payload JSON uniforme (`ErrorResponse`).

---

## 2. Struttura dei Package

```text
com.quicksketch.backend.exception/
├── GlobalExceptionHandler.java
├── auth/
│   ├── InvalidCredentialsException.java
│   ├── InvalidRegistrationDataException.java
│   └── UserAlreadyExistsException.java
├── common/
│   ├── ResourceNotFoundException.java
│   └── UnauthorizedActionException.java
└── game/
    ├── GameRuleViolationException.java
    └── InvalidSketchDataException.java
