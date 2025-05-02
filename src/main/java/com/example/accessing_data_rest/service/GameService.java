package com.example.accessing_data_rest.service;

import com.example.accessing_data_rest.model.Game;
import com.example.accessing_data_rest.model.Player;
import com.example.accessing_data_rest.repositories.GameRepository;
import com.example.accessing_data_rest.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    public List<Game> getGames() {
        List<Game> games = (List<Game>) gameRepository.findAll();
        for (Game game : games) {
            if (game.getPlayers() == null) {
                game.setPlayers(new ArrayList<>());
            }
        }
        return games;
    }

    public List<Game> getOpenGames() {
        List<Game> games = (List<Game>) gameRepository.findAll();
        return games.stream()
            .filter(g -> g.getStatus() == Game.GameStatus.SIGNUP)
            .peek(g -> {
                if (g.getPlayers() == null) {
                    g.setPlayers(new ArrayList<>());
                }
            })
            .collect(Collectors.toList());
    }

    public List<Game> searchGames(String name) {
        List<Game> games = gameRepository.findByName(name);
        for (Game g : games) {
            g.setPlayers(null);  // Strip players to avoid nested deserialization
        }
        return games;
    }

    public Game createGame(String name, int minPlayers, int maxPlayers, String owner) {
        List<Game> existing = gameRepository.findByName(name);
        if (!existing.isEmpty()) {
            throw new RuntimeException("Game already exists");
        }
        Game game = new Game();
        game.setName(name);              // set info
        game.setMinPlayers(minPlayers);
        game.setMaxPlayers(maxPlayers);
        game.setOwner(owner);
        game.setStatus(Game.GameStatus.SIGNUP);
        return gameRepository.save(game);
    }

    public Game getGameById(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));
        if (game.getPlayers() == null) {
            game.setPlayers(new ArrayList<>());
        }
        return game;
    }

    public boolean canJoinGame(Long gameId, String username) {
        Game game = getGameById(gameId);
        
        if (game.getStatus() != Game.GameStatus.SIGNUP) { // signup phase
            return false;
        }

        if (game.getOwner().equals(username)) { // creator cannot join; they should use other methods
            return false;
        }

        if (game.getPlayers() != null && game.getPlayers().size() >= game.getMaxPlayers()) { // not full
            return false;
        }

        return true;
    }

    public boolean isHost(Long gameId, String username) {
        Game game = getGameById(gameId);
        return game.getOwner().equals(username);
    }

    public void leaveGame(Long gameId, String username) {
        Game game = getGameById(gameId);
        if (game.getStatus() != Game.GameStatus.SIGNUP) { // signup phase
            throw new IllegalStateException("Cannot leave game - game is not in signup phase");
        }
        
        if (game.getPlayers() == null || game.getPlayers().isEmpty()) { // player must be in game
            throw new IllegalStateException("You are not a player in this game");
        }
        
        boolean playerFound = false;
        Player playerToRemove = null;
        
        for (Player player : game.getPlayers()) {
            if (player.getUser() != null && player.getUser().getName().equals(username)) {
                playerFound = true;
                playerToRemove = player;
                break;
            }
        }

        if (!playerFound) {
            throw new IllegalStateException("You are not a player in this game");
        }
        
        game.getPlayers().remove(playerToRemove); // delete from game
        if (playerToRemove != null) {
            playerRepository.delete(playerToRemove); // delete from DB
        }
        gameRepository.save(game);
    }

    public void deleteGame(Long gameId, String username) {
        Game game = getGameById(gameId);
        if (!game.getOwner().equals(username)) { // only owner can delete
            throw new IllegalStateException("Only the game owner can delete the game");
        }
        if (game.getStatus() != Game.GameStatus.SIGNUP) { // signup phase
            throw new IllegalStateException("Cannot delete game - game is not in signup phase");
        }

        if (game.getPlayers() != null) { // deletes players
            for (Player player : game.getPlayers()) {
                playerRepository.delete(player);
            }
        }

        gameRepository.delete(game);
    }

    public void startGame(Long gameId, String username) {
        Game game = getGameById(gameId);
        if (!game.getOwner().equals(username)) { // only owner can start
            throw new IllegalStateException("Only the game owner can start the game");
        }
        if (game.getStatus() != Game.GameStatus.SIGNUP) { // signup phase
            throw new IllegalStateException("Game is not in signup phase");
        }
        if (game.getPlayers() == null || game.getPlayers().size() < game.getMinPlayers()) { // must have enough players
            throw new IllegalStateException("Not enough players to start the game");
        }
        
        game.setStatus(Game.GameStatus.ACTIVE);
        gameRepository.save(game);
    }

    public Game joinGame(Long gameId, String username) {
        Game game = getGameById(gameId);
        
        if (game.getStatus() != Game.GameStatus.SIGNUP) { // signup phase
            throw new IllegalStateException("Cannot join game - game is not in signup phase");
        }
        
        boolean alreadyPlayer = false;                    // check if user is already a player
        if (game.getPlayers() != null) {
            for (Player player : game.getPlayers()) {
                if (player.getUser() != null && player.getUser().getName().equals(username)) {
                    alreadyPlayer = true;
                    break;
                }
            }
        }
        
        if (alreadyPlayer) {
            return game;
        }
        
        boolean isHost = game.getOwner().equals(username);
        if (!isHost) {                                      // just checks if game is full
            if (game.getPlayers() != null && game.getPlayers().size() >= game.getMaxPlayers()) {
                throw new IllegalStateException("Cannot join game - game is full");
            }
        }
        
        Player newPlayer = new Player();
        newPlayer.setName(username);                        // convinetly sets player name to username
        
        if (game.getPlayers() == null) {
            game.setPlayers(new ArrayList<>());
        }
        game.getPlayers().add(newPlayer);
        
        newPlayer.setGame(game);
        playerRepository.save(newPlayer);
        
        Game savedGame = gameRepository.save(game);
        
        return savedGame;
    }
} 