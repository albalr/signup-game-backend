package com.example.accessing_data_rest.service;

import com.example.accessing_data_rest.model.Game;
import com.example.accessing_data_rest.model.Player;
import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.repositories.GameRepository;
import com.example.accessing_data_rest.repositories.PlayerRepository;
import com.example.accessing_data_rest.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    /**
     * {@code @Mock} creates a mock object of the specified class.
     * <p>
     * Mock objects are used to simulate the behavior of real objects in a controlled way.
     * They allow us to:
     * <ul>
     *     <li>Define what methods should return when called</li>
     *     <li>Verify that methods were called with specific parameters</li>
     *     <li>Verify the number of times methods were called</li>
     *     <li>Avoid actual database/network calls during testing</li>
     * </ul>
     */
    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private UserRepository userRepository;

    /**
     * {@code @InjectMocks} creates an instance of the class and injects the mocks that are created
     * with the {@code @Mock} annotations into this instance.
     * <p>
     * In this case, it creates a GameService instance and automatically injects the
     * mocked repositories into it.
     * <p>
     * This allows us to test the GameService in isolation while controlling the behavior
     * of its dependencies through the mocks.
     */
    @InjectMocks
    private GameService gameService;

    @Test
    void testGetGames() {
        // Arrange
        Game game1 = new Game();
        game1.setName("Game1");
        Game game2 = new Game();
        game2.setName("Game2");
        List<Game> expectedGames = Arrays.asList(game1, game2);
        when(gameRepository.findAll()).thenReturn(expectedGames);

        // Act
        List<Game> actualGames = gameService.getGames();

        // Assert
        assertEquals(expectedGames.size(), actualGames.size());
        assertEquals(expectedGames, actualGames);
        verify(gameRepository, times(1)).findAll();
    }

    @Test
    void testGetOpenGames() {
        // Arrange
        Game openGame = new Game();
        openGame.setName("Open Game");
        openGame.setStatus(Game.GameStatus.SIGNUP);

        Game closedGame = new Game();
        closedGame.setName("Closed Game");
        closedGame.setStatus(Game.GameStatus.ACTIVE);

        List<Game> allGames = Arrays.asList(openGame, closedGame);
        when(gameRepository.findAll()).thenReturn(allGames);

        // Act
        List<Game> openGames = gameService.getOpenGames();

        // Assert
        assertEquals(1, openGames.size());
        assertEquals(openGame, openGames.get(0));
        verify(gameRepository, times(1)).findAll();
    }

    @Test
    void testCreateGameSuccess() {
        // Arrange
        String gameName = "New Game";
        int minPlayers = 2;
        int maxPlayers = 4;
        String owner = "owner";

        Game savedGame = new Game();
        savedGame.setName(gameName);
        savedGame.setMinPlayers(minPlayers);
        savedGame.setMaxPlayers(maxPlayers);
        savedGame.setOwner(owner);

        User ownerUser = new User();
        ownerUser.setName(owner);

        // Because the createGame method checks if the game already exists firstly
        when(gameRepository.findByName(gameName)).thenReturn(List.of());

        when(gameRepository.save(any(Game.class))).thenReturn(savedGame);
        when(userRepository.findByName(owner)).thenReturn(List.of(ownerUser));
        when(playerRepository.save(any(Player.class))).thenReturn(new Player());

        // Act
        Game result = gameService.createGame(gameName, minPlayers, maxPlayers, owner);

        // Assert
        assertNotNull(result);
        assertEquals(gameName, result.getName());
        assertEquals(minPlayers, result.getMinPlayers());
        assertEquals(maxPlayers, result.getMaxPlayers());
        assertEquals(owner, result.getOwner());
        assertEquals(Game.GameStatus.SIGNUP, result.getStatus());

        verify(gameRepository, times(1)).findByName(gameName);
        verify(gameRepository, times(1)).save(any(Game.class));
        verify(userRepository, times(1)).findByName(owner);
        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    void testCreateGameAlreadyExists() {
        // Arrange
        String existingGameName = "Existing Game";
        Game existingGame = new Game();
        existingGame.setName(existingGameName);
        when(gameRepository.findByName(existingGameName)).thenReturn(List.of(existingGame));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> gameService.createGame(existingGameName, 2, 4, "owner"));
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void testCanJoinGame() {
        // Arrange
        Long gameId = 1L;
        String username = "player";
        Game game = new Game();
        game.setStatus(Game.GameStatus.SIGNUP);
        game.setOwner("owner");
        game.setMaxPlayers(4);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        // Act
        boolean canJoin = gameService.canJoinGame(gameId, username);

        // Assert
        assertTrue(canJoin);
        verify(gameRepository, times(1)).findById(gameId);
    }

    @Test
    void testJoinGameSuccess() {
        // Arrange
        Long gameId = 1L;
        String username = "player";
        Game game = new Game();
        game.setStatus(Game.GameStatus.SIGNUP);
        game.setOwner("owner");
        game.setMaxPlayers(4);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(gameRepository.save(any(Game.class))).thenReturn(game);
        when(playerRepository.save(any(Player.class))).thenReturn(new Player());

        // Act
        Game result = gameService.joinGame(gameId, username);

        // Assert
        assertNotNull(result);
        verify(gameRepository, times(1)).findById(gameId);
        verify(gameRepository, times(1)).save(any(Game.class));
        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    void testStartGameSuccess() {
        // Arrange
        Long gameId = 1L;
        String owner = "owner";
        Game game = new Game();
        game.setStatus(Game.GameStatus.SIGNUP);
        game.setOwner(owner);
        game.setMinPlayers(2);
        game.setPlayers(Arrays.asList(new Player(), new Player()));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(gameRepository.save(any(Game.class))).thenReturn(game);

        // Act
        gameService.startGame(gameId, owner);

        // Assert
        assertEquals(Game.GameStatus.ACTIVE, game.getStatus());
        verify(gameRepository, times(1)).findById(gameId);
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    void testGameOwnership() {
        // Arrange
        String gameName = "Test Game";
        String owner = "owner";
        Game game = new Game();
        game.setName(gameName);
        game.setOwner(owner);
        game.setStatus(Game.GameStatus.SIGNUP);

        when(gameRepository.findById(anyLong())).thenReturn(Optional.of(game));

        // 1. Verify owner can start game
        assertTrue(gameService.isHost(game.getUid(), owner));

        // 2. Verify non-owner cannot start game
        assertFalse(gameService.isHost(game.getUid(), "nonowner"));

        // 3. Verify owner can delete game
        gameService.deleteGame(game.getUid(), owner);
        verify(gameRepository, times(1)).delete(game);

        // 4. Verify non-owner cannot delete game
        Game anotherGame = new Game();
        anotherGame.setOwner("owner2");
        when(gameRepository.findById(anyLong())).thenReturn(Optional.of(anotherGame));

        assertThrows(IllegalStateException.class, () -> gameService.deleteGame(anotherGame.getUid(), "nonowner"));
    }

    @Test
    void testGameStateTransitions() {
        // Arrange
        Game game = new Game();
        game.setStatus(Game.GameStatus.INITIAL);
        game.setOwner("owner");
        game.setMinPlayers(2);
        game.setPlayers(Arrays.asList(new Player(), new Player()));

        when(gameRepository.findById(anyLong())).thenReturn(Optional.of(game));
        when(gameRepository.save(any(Game.class))).thenReturn(game);

        // 1. Start game from INITIAL state
        assertThrows(IllegalStateException.class, () -> gameService.startGame(game.getUid(), "owner"));

        // 2. Change to SIGNUP state
        game.setStatus(Game.GameStatus.SIGNUP);
        gameService.startGame(game.getUid(), "owner");
        assertEquals(Game.GameStatus.ACTIVE, game.getStatus());

        // 3. Try to start already active game
        assertThrows(IllegalStateException.class, () -> gameService.startGame(game.getUid(), "owner"));

        // 4. Try to join active game
        assertThrows(IllegalStateException.class, () -> gameService.joinGame(game.getUid(), "newplayer"));
    }
}