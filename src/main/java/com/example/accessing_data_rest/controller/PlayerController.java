package com.example.accessing_data_rest.controller;

import com.example.accessing_data_rest.model.Player;
import com.example.accessing_data_rest.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    // GET
    @GetMapping(value = "/players", produces = "application/json")
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    @GetMapping(value = "/players/search", produces = "application/json")
    public List<Player> searchPlayers(@RequestParam("name") String name) {
        return playerService.searchPlayers(name);
    }

    // POST
    // this is all necessary for HAL links to work.
    @PostMapping(value = "/player", consumes = "application/json")
    public ResponseEntity<?> signUpToGame(@RequestBody Map<String, Object> playerData) {
        try {
            String name = (String) playerData.get("name");       // get refs
            String userRef = (String) playerData.get("user");
            String gameRef = (String) playerData.get("game");
            
            Long userId = extractIdFromHalReference(userRef);        // get ID from HAL
            Long gameId = extractIdFromHalReference(gameRef);
            
            playerService.createPlayerFromIds(name, userId, gameId); // create player
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) { // for expected errors
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {             // for unexpected errors
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping(value = "/players/user/{userId}", produces = "application/json")
    public List<Player> getPlayersByUser(@PathVariable Long userId) {
        return playerService.getPlayersByUser(userId);
    }

    // saves some lines of code to do helper method
    private Long extractIdFromHalReference(String reference) {
        if (reference == null) {
            throw new IllegalStateException("Reference cannot be null");
        }
        try {
            String[] parts = reference.split("/");
            return Long.parseLong(parts[parts.length - 1]);
        } catch (Exception e) {
            throw new IllegalStateException("Invalid reference format: " + reference);
        }
    }
} 