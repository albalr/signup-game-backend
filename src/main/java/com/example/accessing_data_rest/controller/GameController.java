package com.example.accessing_data_rest.controller;

import com.example.accessing_data_rest.controller.dto.GameCreationRequest;
import com.example.accessing_data_rest.model.Game;
import com.example.accessing_data_rest.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    @GetMapping(value = "/opengames", produces = "application/json")
    public List<Game> getOpenGames() {
        return gameService.getOpenGames();
    }

    @PostMapping(value = "/creategame", consumes = "application/json", produces = "application/json")
    public Game createGame(@RequestBody GameCreationRequest request) {
        return gameService.createGame(request);
    }

}

