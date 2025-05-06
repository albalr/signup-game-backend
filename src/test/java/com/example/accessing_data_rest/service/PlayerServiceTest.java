package com.example.accessing_data_rest.service;

import com.example.accessing_data_rest.model.Game;
import com.example.accessing_data_rest.model.Player;
import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.repositories.PlayerRepository;
import com.example.accessing_data_rest.repositories.UserRepository;
import com.example.accessing_data_rest.repositories.GameRepository;
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
class PlayerServiceTest {

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
    private PlayerRepository playerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameService gameService;

    /**
     * {@code @InjectMocks} creates an instance of the class and injects the mocks that are created
     * with the {@code @Mock} annotations into this instance.
     * <p>
     * In this case, it creates a PlayerService instance and automatically injects the
     * mocked repositories and services into it.
     * <p>
     * This allows us to test the PlayerService in isolation while controlling the behavior
     * of its dependencies through the mocks.
     */
    @InjectMocks
    private PlayerService playerService;

    @Test
    void testGetAllPlayers() {
        // Arrange
        Player player1 = new Player();
        player1.setName("Player1");
        Player player2 = new Player();
        player2.setName("Player2");
        List<Player> expectedPlayers = Arrays.asList(player1, player2);
        when(playerRepository.findAll()).thenReturn(expectedPlayers);

        // Act
        List<Player> actualPlayers = playerService.getAllPlayers();

        // Assert
        assertEquals(expectedPlayers.size(), actualPlayers.size());
        assertEquals(expectedPlayers, actualPlayers);
        verify(playerRepository, times(1)).findAll();
    }

    @Test
    void testSearchPlayers() {
        // Arrange
        String searchName = "Test";
        Player player = new Player();
        player.setName(searchName);
        List<Player> expectedPlayers = List.of(player);
        when(playerRepository.findByName(searchName)).thenReturn(expectedPlayers);

        // Act
        List<Player> actualPlayers = playerService.searchPlayers(searchName);

        // Assert
        assertEquals(expectedPlayers.size(), actualPlayers.size());
        assertEquals(expectedPlayers, actualPlayers);
        verify(playerRepository, times(1)).findByName(searchName);
    }

    @Test
    void testCreatePlayerFromIdsSuccess() {
        // Arrange
        String playerName = "New Player";
        Long userId = 1L;
        Long gameId = 1L;

        User user = new User();
        user.setUid(userId);

        Game game = new Game();
        game.setUid(gameId);
        game.setStatus(Game.GameStatus.SIGNUP);
        game.setMaxPlayers(4);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        // Simulate that the user has not joined any game yet
        when(playerRepository.findByUserUid(userId)).thenReturn(List.of());

        when(playerRepository.save(any(Player.class))).thenReturn(new Player());
        when(gameRepository.save(any(Game.class))).thenReturn(game);

        // Act
        playerService.createPlayerFromIds(playerName, userId, gameId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(gameRepository, times(1)).findById(gameId);
        verify(playerRepository, times(1)).findByUserUid(userId);
        verify(playerRepository, times(1)).save(any(Player.class));
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    void testCreatePlayerFromIdsUserNotFound() {
        // Arrange
        String playerName = "New Player";
        Long userId = 1L;
        Long gameId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> playerService.createPlayerFromIds(playerName, userId, gameId));
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void testCreatePlayerFromIdsGameNotFound() {
        // Arrange
        String playerName = "New Player";
        Long userId = 1L;
        Long gameId = 1L;

        User user = new User();
        user.setUid(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> playerService.createPlayerFromIds(playerName, userId, gameId));
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void testCreatePlayerFromIdsAlreadyJoined() {
        // Arrange
        String playerName = "New Player";
        Long userId = 1L;
        Long gameId = 1L;

        User user = new User();
        user.setUid(userId);

        Game game = new Game();
        game.setUid(gameId);

        Player existingPlayer = new Player();
        existingPlayer.setGame(game);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        // Simulate that the user has already joined the game
        when(playerRepository.findByUserUid(userId)).thenReturn(List.of(existingPlayer));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> playerService.createPlayerFromIds(playerName, userId, gameId));
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void testCreatePlayerSuccess() {
        // Arrange
        Player player = new Player();
        player.setName("New Player");

        User user = new User();
        user.setUid(1L);
        user.setName("Test User");
        player.setUser(user);

        Game game = new Game();
        game.setUid(1L);
        player.setGame(game);

        when(userRepository.findById(user.getUid())).thenReturn(Optional.of(user));
        when(gameRepository.findById(game.getUid())).thenReturn(Optional.of(game));
        when(gameService.canJoinGame(game.getUid(), user.getName())).thenReturn(true);
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        // Act
        Player result = playerService.createPlayer(player);

        // Assert
        assertNotNull(result);
        assertEquals(player.getName(), result.getName());
        verify(userRepository, times(1)).findById(user.getUid());
        verify(gameRepository, times(1)).findById(game.getUid());
        verify(gameService, times(1)).canJoinGame(game.getUid(), user.getName());
        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    void testGetPlayersByUser() {
        // Arrange
        Long userId = 1L;
        Player player1 = new Player();
        player1.setName("Player1");
        Player player2 = new Player();
        player2.setName("Player2");
        List<Player> expectedPlayers = Arrays.asList(player1, player2);
        when(playerRepository.findByUserUid(userId)).thenReturn(expectedPlayers);

        // Act
        List<Player> actualPlayers = playerService.getPlayersByUser(userId);

        // Assert
        assertEquals(expectedPlayers.size(), actualPlayers.size());
        assertEquals(expectedPlayers, actualPlayers);
        verify(playerRepository, times(1)).findByUserUid(userId);
    }

    @Test
    void testPlayerGameLifecycle() {
        // Arrange
        String playerName = "testplayer";
        Long userId = 1L;
        Long gameId = 1L;

        User user = new User();
        user.setUid(userId);
        user.setName(playerName);

        Game game = new Game();
        game.setUid(gameId);
        game.setStatus(Game.GameStatus.SIGNUP);
        game.setMaxPlayers(4);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(playerRepository.findByUserUid(userId)).thenReturn(List.of());
        when(playerRepository.save(any(Player.class))).thenReturn(new Player());
        when(gameRepository.save(any(Game.class))).thenReturn(game);

        // 1. Create player in game
        playerService.createPlayerFromIds(playerName, userId, gameId);
        verify(playerRepository, times(1)).save(any(Player.class));

        // 2. Try to create same player again
        Player existingPlayer = new Player();
        existingPlayer.setGame(game);
        when(playerRepository.findByUserUid(userId)).thenReturn(List.of(existingPlayer));
        assertThrows(IllegalStateException.class, () -> playerService.createPlayerFromIds(playerName, userId, gameId));

        // 3. Try to join full game
        game.setMaxPlayers(1);
        game.setPlayers(List.of(existingPlayer));
        assertThrows(IllegalStateException.class, () -> playerService.createPlayerFromIds("newplayer", 2L, gameId));
    }

    @Test
    void testPlayerGameStateTransitions() {
        // Arrange
        String playerName = "testplayer";
        Long userId = 1L;
        Long gameId = 1L;

        User user = new User();
        user.setUid(userId);
        user.setName(playerName);

        Game game = new Game();
        game.setUid(gameId);
        game.setStatus(Game.GameStatus.ACTIVE); // Game is already active

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        // Try to join active game
        assertThrows(IllegalStateException.class, () -> playerService.createPlayerFromIds(playerName, userId, gameId));

        // Change game state to SIGNUP
        game.setStatus(Game.GameStatus.SIGNUP);
        when(playerRepository.findByUserUid(userId)).thenReturn(List.of());
        when(playerRepository.save(any(Player.class))).thenReturn(new Player());
        when(gameRepository.save(any(Game.class))).thenReturn(game);

        // Now should be able to join
        playerService.createPlayerFromIds(playerName, userId, gameId);
        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    void testPlayerGameRelationships() {
        // Arrange
        String playerName = "testplayer";
        long userId = 1L;
        long gameId = 1L;

        User user = new User();
        user.setUid(userId);
        user.setName(playerName);

        Game game = new Game();
        game.setUid(gameId);
        game.setStatus(Game.GameStatus.SIGNUP);

        Player player = new Player();
        player.setName(playerName);
        player.setUser(user);
        player.setGame(game);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(gameService.canJoinGame(gameId, playerName)).thenReturn(true);
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        // Act
        Player createdPlayer = playerService.createPlayer(player);

        // Assert
        assertEquals(user, createdPlayer.getUser());
        assertEquals(game, createdPlayer.getGame());
        assertEquals(playerName, createdPlayer.getName());
    }

    @Test
    void testPlayerGameQueries() {
        // Arrange
        Long userId = 1L;
        String searchName = "test";

        Player player1 = new Player();
        player1.setName("testplayer1");
        Player player2 = new Player();
        player2.setName("testplayer2");
        Player player3 = new Player();
        player3.setName("otherplayer");

        List<Player> allPlayers = Arrays.asList(player1, player2, player3);
        List<Player> matchingPlayers = Arrays.asList(player1, player2);
        List<Player> userPlayers = Arrays.asList(player1, player3);

        when(playerRepository.findAll()).thenReturn(allPlayers);
        when(playerRepository.findByName(searchName)).thenReturn(matchingPlayers);
        when(playerRepository.findByUserUid(userId)).thenReturn(userPlayers);

        // 1. Test getting all players
        List<Player> allRetrievedPlayers = playerService.getAllPlayers();
        assertEquals(3, allRetrievedPlayers.size());

        // 2. Test searching players
        List<Player> searchResults = playerService.searchPlayers(searchName);
        assertEquals(2, searchResults.size());
        assertTrue(searchResults.contains(player1));
        assertTrue(searchResults.contains(player2));
        assertFalse(searchResults.contains(player3));

        // 3. Test getting players by user
        List<Player> userRetrievedPlayers = playerService.getPlayersByUser(userId);
        assertEquals(2, userRetrievedPlayers.size());
        assertTrue(userRetrievedPlayers.contains(player1));
        assertTrue(userRetrievedPlayers.contains(player3));
        assertFalse(userRetrievedPlayers.contains(player2));
    }
}