package com.example.accessing_data_rest.controller;

import com.example.accessing_data_rest.model.Game;
import com.example.accessing_data_rest.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    // GETs
    @GetMapping(value = "/allgames", produces = "application/json")
    public List<Game> getAllGames() {
        return gameService.getGames();
    }

    @GetMapping(value = "/opengames", produces = "application/json")   // for SOG
    public List<Game> getOpenGames() {
        return gameService.getOpenGames();
    }
    
    @GetMapping(value = "/searchgames", produces = "application/json") // NB: Just searches by name. could be 
    public List<Game> searchGames(@RequestParam("name") String name) { // renamed to getGamesByName or smth 
        return gameService.searchGames(name);                          
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public Game getGameById(@PathVariable Long id) {
        return gameService.getGameById(id);
    }

    @GetMapping(value = "/{id}/canjoin", produces = "application/json")
    public boolean canJoinGame(@PathVariable Long id, @RequestParam("username") String username) {
        return gameService.canJoinGame(id, username);
    }

    @GetMapping(value = "/{id}/ishost", produces = "application/json")
    public boolean isHost(@PathVariable Long id, @RequestParam("username") String username) {
        return gameService.isHost(id, username);
    }

    // POSTs 
    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public Game createGame(@RequestBody Game game) {
        return gameService.createGame(
            game.getName(),
            game.getMinPlayers(),
            game.getMaxPlayers(),
            game.getOwner()
        );
    }

    @PostMapping(value = "/{id}/join")
    public ResponseEntity<?> joinGame(@PathVariable Long id, @RequestParam("username") String username) {
        try {
            Game updatedGame = gameService.joinGame(id, username);
            return ResponseEntity.ok(updatedGame);
        } catch (IllegalStateException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "An unexpected error occurred while joining the game"));
        }
    }

    @PostMapping(value = "/{id}/leave")
    public ResponseEntity<?> leaveGame(@PathVariable Long id, @RequestParam("username") String username) {
        try {
            gameService.leaveGame(id, username);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "An unexpected error occurred while leaving the game"));
        }
    }

    @PostMapping(value = "/{id}/start")
    public ResponseEntity<?> startGame(@PathVariable Long id, @RequestParam("username") String username) {
        try {
            gameService.startGame(id, username);
            return ResponseEntity.ok()
                .body(Map.of("message", "Game started successfully. [Game implementation not part of this course]"));
        } catch (IllegalStateException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "An unexpected error occurred while starting the game"));
        }
    }

    
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteGame(@PathVariable Long id, @RequestParam("username") String username) {
        try {
            gameService.deleteGame(id, username);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "An unexpected error occurred while deleting the game"));
        }
    }
} 