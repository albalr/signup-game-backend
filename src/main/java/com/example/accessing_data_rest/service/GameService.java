package com.example.accessing_data_rest.service;

import com.example.accessing_data_rest.controller.dto.GameCreationRequest;
import com.example.accessing_data_rest.model.Game;
import com.example.accessing_data_rest.model.GameState;
import com.example.accessing_data_rest.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    public List<Game> getOpenGames() {
        return gameRepository.findByStateIs(GameState.SIGNUP);
    }

    public Game createGame(GameCreationRequest request) {
        Game game = new Game();
        game.setName(request.name);
        game.setMinPlayers(request.minPlayers);
        game.setMaxPlayers(request.maxPlayers);
        game.setOwner(request.ownerUsername);
        game.setState(GameState.SIGNUP);

        return gameRepository.save(game);
    }

}
