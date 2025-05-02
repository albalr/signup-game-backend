package com.example.accessing_data_rest.service;

import com.example.accessing_data_rest.model.Player; // NB: unlike GameService and UserService, PlayerService imports both
import com.example.accessing_data_rest.model.User;   // User and Game models and repos since it is the connecting layer 
import com.example.accessing_data_rest.model.Game;   // between the two. See illustration in Assignment 7a pdf
import com.example.accessing_data_rest.repositories.PlayerRepository;
import com.example.accessing_data_rest.repositories.UserRepository;
import com.example.accessing_data_rest.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameService gameService;

    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        playerRepository.findAll().forEach(players::add);
        return players;
    }

    public List<Player> searchPlayers(String name) {
        return playerRepository.findByName(name);
    }

    @Transactional // = either implement all or nothing in DB
    public void createPlayerFromIds(String name, Long userId, Long gameId) {
        try {
            // CHECKS
            User user = userRepository.findById(userId) // user exists
                    .orElseThrow(() -> new IllegalStateException("User not found with ID: " + userId));
            
            Game game = gameRepository.findById(gameId) // game exists
                    .orElseThrow(() -> new IllegalStateException("Game not found with ID: " + gameId));
            
            List<Player> existingPlayers = playerRepository.findByUserUid(userId); // already joined
            boolean alreadyJoined = existingPlayers.stream() 
                    .anyMatch(p -> p.getGame() != null && p.getGame().getUid() == gameId);
            if (alreadyJoined) {
                throw new IllegalStateException("You have already joined this game");
            }

            if (game.getStatus() != Game.GameStatus.SIGNUP) { // signup phase  
                throw new IllegalStateException("This game is not accepting new players at the moment");
            }

            if (game.getPlayers() != null && game.getPlayers().size() >= game.getMaxPlayers()) { // is full
                throw new IllegalStateException("This game is already full");
            }

            Player player = new Player();  // sets player info
            player.setName(name);
            player.setUser(user);
            player.setGame(game);
            
            if (game.getPlayers() == null) {
                game.setPlayers(new ArrayList<>()); // sets player list
            }
            
            game.getPlayers().add(player); // adds players
            playerRepository.save(player);
            gameRepository.save(game);
        } catch (IllegalStateException e) {
            throw e; // no special handling
        } catch (Exception e) {
            throw new RuntimeException("Failed to create player: " + e.getMessage());
        }
    }

    public Player createPlayer(Player player) {
        User user = userRepository.findById(player.getUser().getUid()) //  user exists
                .orElseThrow(() -> new RuntimeException("User not found"));
        Game game = gameRepository.findById(player.getGame().getUid()) // game exists
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (!gameService.canJoinGame(game.getUid(), user.getName())) { // can join game
            throw new RuntimeException("Cannot join game - game is not open, user is creator, or game is full");
        }
        
        player.setUser(user); // set relationship
        player.setGame(game);

        return playerRepository.save(player); // finally, saves player
    }

    // NB: Might seem random and like it's a getter that's supposed to be in Player.java,
    // but it's in the right place. UserService and GameService 
    // *could* have the same methods in theory but it's not neeeded per se. 
    // compare w/ searchPlayer, which is in main body.
    public List<Player> getPlayersByUser(Long userId) {
        return playerRepository.findByUserUid(userId);
    }
} 