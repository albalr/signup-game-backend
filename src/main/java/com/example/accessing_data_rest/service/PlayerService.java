package com.example.accessing_data_rest.service;

import com.example.accessing_data_rest.model.Player;
import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.model.Game;
import com.example.accessing_data_rest.repositories.PlayerRepository;
import com.example.accessing_data_rest.repositories.UserRepository;
import com.example.accessing_data_rest.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        playerRepository.findAll().forEach(players::add);
        return players;
    }

    public List<Player> searchPlayers(String name) {
        return playerRepository.findByName(name);
    }

    public Player createPlayer(Player player) {
        // Ensure the user and game exist
        User user = userRepository.findById(player.getUser().getUid())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Game game = gameRepository.findById(player.getGame().getUid())
                .orElseThrow(() -> new RuntimeException("Game not found"));

        // Set the relationships
        player.setUser(user);
        player.setGame(game);

        // Save the player
        return playerRepository.save(player);
    }

    public List<Player> getPlayersByUser(Long userId) {
        return playerRepository.findByUserUid(userId);
    }
} 