package com.quicksketch.backend;

import com.quicksketch.backend.dto.auth.LoginRequest;
import com.quicksketch.backend.dto.auth.RegisterRequest;
import com.quicksketch.backend.dto.guess.GuessSessionResponse;
import com.quicksketch.backend.dto.sketch.CompleteSketchRequest;
import com.quicksketch.backend.dto.stats.GlobalLeaderboardsResponse;
import com.quicksketch.backend.exception.auth.InvalidCredentialsException;
import com.quicksketch.backend.exception.auth.InvalidRegistrationDataException;
import com.quicksketch.backend.exception.auth.UserAlreadyExistsException;
import com.quicksketch.backend.exception.common.UnauthorizedActionException;
import com.quicksketch.backend.exception.game.GameRuleViolationException;
import com.quicksketch.backend.exception.game.InvalidSketchDataException;
import com.quicksketch.backend.mapper.SketchMapper;
import com.quicksketch.backend.model.Guess;
import com.quicksketch.backend.model.Sketch;
import com.quicksketch.backend.model.User;
import com.quicksketch.backend.repository.GuessRepository;
import com.quicksketch.backend.repository.SketchRepository;
import com.quicksketch.backend.repository.UserRepository;
import com.quicksketch.backend.security.JwtUtils;
import com.quicksketch.backend.service.AuthService;
import com.quicksketch.backend.service.impl.GuessServiceImpl;
import com.quicksketch.backend.service.impl.SketchServiceImpl;
import com.quicksketch.backend.service.impl.StatsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuickSketchApplicationStressTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SketchRepository sketchRepository;

    @Mock
    private GuessRepository guessRepository;

    @Mock
    private SketchMapper sketchMapper;

    @Mock
    private JwtUtils jwtUtils;

    private PasswordEncoder passwordEncoder;
    private AuthService authService;
    private SketchServiceImpl sketchService;
    private GuessServiceImpl guessService;
    private StatsServiceImpl statsService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(userRepository, passwordEncoder, jwtUtils);
        sketchService = new SketchServiceImpl(sketchRepository, guessRepository, userRepository, sketchMapper);
        guessService = new GuessServiceImpl(guessRepository, sketchRepository, userRepository);
        statsService = new StatsServiceImpl(userRepository);
    }

    // =========================================================================
    // TEST 1: Registrazione con username già esistente -> 409 Conflict
    // =========================================================================
    @Test
    @DisplayName("01. Registrazione fallisce con UserAlreadyExistsException se lo username è già occupato")
    void test01_Register_DuplicateUsername_ThrowsConflict() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("gennaro");
        req.setEmail("gennaro@unina.it");
        req.setPassword("Sicura123");

        when(userRepository.existsByUsername("gennaro")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any(User.class));
    }

    // =========================================================================
    // TEST 2: Registrazione con email malformata o password debole -> 400
    // =========================================================================
    @Test
    @DisplayName("02. Registrazione fallisce con InvalidRegistrationDataException se password o email non rispettano i vincoli")
    void test02_Register_WeakPasswordOrInvalidEmail_ThrowsBadRequest() {
        RegisterRequest badEmailReq = new RegisterRequest();
        badEmailReq.setUsername("nuovoUtente");
        badEmailReq.setEmail("emailSenzaChiocciola.it");
        badEmailReq.setPassword("Sicura123");

        assertThrows(InvalidRegistrationDataException.class, () -> authService.register(badEmailReq));

        RegisterRequest weakPwdReq = new RegisterRequest();
        weakPwdReq.setUsername("nuovoUtente");
        weakPwdReq.setEmail("valido@unina.it");
        weakPwdReq.setPassword("debole"); // < 8 caratteri, nessuna maiuscola, nessun numero

        assertThrows(InvalidRegistrationDataException.class, () -> authService.register(weakPwdReq));
        verify(userRepository, never()).save(any(User.class));
    }

    // =========================================================================
    // TEST 3: Login con password errata -> 401 Unauthorized
    // =========================================================================
    @Test
    @DisplayName("03. Login fallisce con InvalidCredentialsException se la password non corrisponde all'hash")
    void test03_Login_WrongPassword_ThrowsUnauthorized() {
        User existingUser = new User();
        existingUser.setUsername("mario");
        existingUser.setPassword(passwordEncoder.encode("PasswordCorretta1"));

        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername("mario");
        loginReq.setPassword("PasswordSbagliata9");

        when(userRepository.findByUsername("mario")).thenReturn(Optional.of(existingUser));

        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginReq));
    }

    // =========================================================================
    // TEST 4: Completamento sketch da parte di un utente diverso dall'autore -> 403
    // =========================================================================
    @Test
    @DisplayName("04. Completamento sketch vietato (UnauthorizedActionException) se richiesto da un utente non autore")
    void test04_CompleteSketch_ByNonAuthor_ThrowsUnauthorizedAction() {
        Sketch sketch = new Sketch("autoreReale", "castello");
        sketch.setId("sketch-100");
        sketch.setCompleted(false);

        CompleteSketchRequest req = new CompleteSketchRequest();
        req.setImageData("data:image/png;base64,iVBORw0KGgo...");

        when(sketchRepository.findById("sketch-100")).thenReturn(Optional.of(sketch));

        assertThrows(UnauthorizedActionException.class,
                () -> sketchService.completeSketch("sketch-100", "utenteIntruso", req));
        verify(sketchRepository, never()).save(any(Sketch.class));
    }

    // =========================================================================
    // TEST 5: Completamento sketch con immagine vuota/mancante -> 400
    // =========================================================================
    @Test
    @DisplayName("05. Completamento sketch rifiutato (InvalidSketchDataException) se i dati Base64 sono vuoti")
    void test05_CompleteSketch_WithoutImageData_ThrowsInvalidSketchData() {
        Sketch sketch = new Sketch("autoreReale", "vulcano");
        sketch.setId("sketch-101");
        sketch.setCompleted(false);

        CompleteSketchRequest emptyReq = new CompleteSketchRequest();
        emptyReq.setImageData("   ");

        when(sketchRepository.findById("sketch-101")).thenReturn(Optional.of(sketch));

        assertThrows(InvalidSketchDataException.class,
                () -> sketchService.completeSketch("sketch-101", "autoreReale", emptyReq));
    }

    // =========================================================================
    // TEST 6: Doppio completamento dello stesso sketch -> 400 GameRuleViolation
    // =========================================================================
    @Test
    @DisplayName("06. Doppio completamento dello stesso sketch bloccato da GameRuleViolationException")
    void test06_CompleteSketch_AlreadyCompleted_ThrowsGameRuleViolation() {
        Sketch sketch = new Sketch("autoreReale", "razzo");
        sketch.setId("sketch-102");
        sketch.setCompleted(true);

        CompleteSketchRequest req = new CompleteSketchRequest();
        req.setImageData("data:image/png;base64,AAAA...");

        when(sketchRepository.findById("sketch-102")).thenReturn(Optional.of(sketch));

        assertThrows(GameRuleViolationException.class,
                () -> sketchService.completeSketch("sketch-102", "autoreReale", req));
    }

    // =========================================================================
    // TEST 7: Tentativo di indovinare un proprio sketch (Anti-Cheating) -> 403
    // =========================================================================
    @Test
    @DisplayName("07. Un giocatore non può avviare una sessione di guess sul proprio disegno")
    void test07_StartGuess_OwnSketch_ThrowsUnauthorizedAction() {
        Sketch ownSketch = new Sketch("gennaro", "chitarra");
        ownSketch.setId("sketch-200");
        ownSketch.setCompleted(true);

        when(sketchRepository.findById("sketch-200")).thenReturn(Optional.of(ownSketch));

        assertThrows(UnauthorizedActionException.class,
                () -> guessService.startOrResumeGuess("gennaro", "sketch-200"));
        verify(guessRepository, never()).save(any(Guess.class));
    }

    // =========================================================================
    // TEST 8: Vittoria con normalizzazione input e aggiornamento statistiche
    // =========================================================================
    @Test
    @DisplayName("08. Tentativo corretto (anche con maiuscole/spazi) chiude in WON e aggiorna le statistiche di entrambi gli utenti")
    void test08_SubmitAttempt_ExactMatchWithSpacesAndCase_WinsAndUpdatesEloStats() {
        Sketch sketch = new Sketch("autore", "giraffa");
        sketch.setId("sketch-300");
        sketch.setCompleted(true);

        Guess activeGuess = new Guess("sketch-300", "giocatore");
        activeGuess.setFinished(false);
        activeGuess.setAttemptsUsed(0);

        User player = new User();
        player.setUsername("giocatore");

        User author = new User();
        author.setUsername("autore");

        when(sketchRepository.findById("sketch-300")).thenReturn(Optional.of(sketch));
        when(guessRepository.findByUsernameAndSketchId("giocatore", "sketch-300"))
                .thenReturn(Optional.of(activeGuess));
        when(guessRepository.save(any(Guess.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.findByUsername("giocatore")).thenReturn(Optional.of(player));
        when(userRepository.findByUsername("autore")).thenReturn(Optional.of(author));

        // Input sporco con spazi e maiuscole miste
        GuessSessionResponse response = guessService.submitAttempt("giocatore", "sketch-300", "   GiRaFfA  ");

        assertEquals("WON", response.getStatus());
        assertEquals("giraffa", response.getSecretWord());
        assertEquals(1, player.getWordsGuessed());
        assertEquals(1, player.getTotalGuessAttemptsUsed());
        assertEquals(1, author.getSketchesPlayedByOthers());
        assertEquals(1, author.getSketchesGuessedByOthers());
    }

    // =========================================================================
    // TEST 9: Esaurimento dei 10 tentativi -> Sconfitta automatica e parola svelata
    // =========================================================================
    @Test
    @DisplayName("09. Al 10° tentativo errato la partita passa a LOST, svela la parola e incrementa wordsFailed")
    void test09_SubmitAttempt_TenthWrongAttempt_ExhaustsAttemptsAndFails() {
        Sketch sketch = new Sketch("autore", "sottomarino");
        sketch.setId("sketch-400");
        sketch.setCompleted(true);

        Guess activeGuess = new Guess("sketch-400", "giocatore");
        activeGuess.setFinished(false);
        activeGuess.setAttemptsUsed(9);
        for (int i = 1; i <= 9; i++) {
            activeGuess.getAttemptedWords().add("parola_errata_" + i);
        }

        User player = new User();
        player.setUsername("giocatore");

        User author = new User();
        author.setUsername("autore");

        when(sketchRepository.findById("sketch-400")).thenReturn(Optional.of(sketch));
        when(guessRepository.findByUsernameAndSketchId("giocatore", "sketch-400"))
                .thenReturn(Optional.of(activeGuess));
        when(guessRepository.save(any(Guess.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.findByUsername("giocatore")).thenReturn(Optional.of(player));
        when(userRepository.findByUsername("autore")).thenReturn(Optional.of(author));

        // 10° tentativo errato
        GuessSessionResponse response = guessService.submitAttempt("giocatore", "sketch-400", "nave");

        assertEquals("LOST", response.getStatus());
        assertEquals(0, response.getAttemptsRemaining());
        assertEquals("sottomarino", response.getSecretWord());
        assertEquals(1, player.getWordsFailed());
        assertEquals(1, author.getSketchesPlayedByOthers());
        assertEquals(0, author.getSketchesGuessedByOthers());
    }

    // =========================================================================
    // TEST 10: Stress test matematico classifica Elo Bayesiana
    // =========================================================================
    @Test
    @DisplayName("10. L'algoritmo Elo Bayesiano impedisce che 1 sola vittoria (100%) superi un giocatore veterano (20/25)")
    void test10_StatsLeaderboard_BayesianEloPreventsSingleGameOutlier() {
        User rookieOneWin = new User();
        rookieOneWin.setUsername("novizio_fortunato");
        rookieOneWin.setWordsGuessed(1);
        rookieOneWin.setWordsFailed(0);
        rookieOneWin.setTotalGuessAttemptsUsed(1);

        User veteran = new User();
        veteran.setUsername("veterano_costante");
        veteran.setWordsGuessed(20);
        veteran.setWordsFailed(5);
        veteran.setTotalGuessAttemptsUsed(45);

        when(userRepository.findAll()).thenReturn(List.of(rookieOneWin, veteran));

        GlobalLeaderboardsResponse leaderboards = statsService.getGlobalLeaderboards();

        assertEquals(2, leaderboards.getTopGuessers().size());
        assertEquals("veterano_costante", leaderboards.getTopGuessers().get(0).getUsername());
        assertEquals(1, leaderboards.getTopGuessers().get(0).getRank());
        assertTrue(leaderboards.getTopGuessers().get(0).getGuesserElo()
                > leaderboards.getTopGuessers().get(1).getGuesserElo());
    }
}