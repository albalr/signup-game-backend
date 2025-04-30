package com.example.accessing_data_rest.controller;

import com.example.accessing_data_rest.model.Player;
import com.example.accessing_data_rest.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @GetMapping(value = "/allplayers", produces = "application/json")
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    @GetMapping(value = "/searchplayers", produces = "application/json")
    public List<Player> searchPlayers(@RequestParam("name") String name) {
        return playerService.searchPlayers(name);
    }

    @PostMapping(value = "/signup", consumes = "application/json", produces = "application/json")
    public Player signUpToGame(@RequestBody Player player) {
        return playerService.createPlayer(player);
    }

    @GetMapping(value = "/user/{userId}", produces = "application/json")
    public List<Player> getPlayersByUser(@PathVariable Long userId) {
        return playerService.getPlayersByUser(userId);
    }
} 