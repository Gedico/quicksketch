# Autenticazione e Sicurezza (QuickSketch Backend)

## Panoramica
L'autenticazione è gestita tramite **Spring Security** in modalità **Stateless**, utilizzando **JSON Web Tokens (JWT)** per autorizzare le chiamate tra client e server.

## Componenti Principali
1. **User & UserRepository**: Mappatura MongoDB dell'utente (username unico, email unica, password cifrata).
2. **SecurityConfig**: 
   - Disabilita CSRF per architettura REST.
   - Imposta la gestione sessione su `STATELESS`.
   - Mantiene pubbliche le rotte `/api/auth/**` e richiede autenticazione per le altre.
3. **JwtUtils**: Genera e valida token JWT firmati con chiave HMAC-SHA.
4. **AuthService & AuthController**:
   - `POST /api/auth/register`: Cifra la password con `BCryptPasswordEncoder` e salva il nuovo utente.
   - `POST /api/auth/login`: Valida le credenziali e rilascia il token JWT Bearer.

## Modello dei Payload (DTO)
- **RegisterRequest**: `username` (3-20 char), `email` (valid format), `password` (min 6 char).
- **LoginRequest**: `username`, `password`.
- **AuthResponse**: `token`, `username`, `email`.
