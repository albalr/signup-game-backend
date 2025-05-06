package com.example.accessing_data_rest.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

class GameTest {

    @Test
    void testGameCreation() {
        Game game = new Game();
        game.setName("Test Game");
        game.setMinPlayers(2);
        game.setMaxPlayers(4);
        game.setOwner("testowner");
        game.setStatus(Game.GameStatus.SIGNUP);

        assertEquals("Test Game", game.getName());
        assertEquals(2, game.getMinPlayers());
        assertEquals(4, game.getMaxPlayers());
        assertEquals("testowner", game.getOwner());
        assertEquals(Game.GameStatus.SIGNUP, game.getStatus());
    }

    @Test
    void testGamePlayers() {
        Game game = new Game();
        List<Player> players = new ArrayList<>();
        Player player = new Player();
        players.add(player);
        game.setPlayers(players);

        assertEquals(1, game.getPlayers().size());
        assertEquals(player, game.getPlayers().get(0));
    }

    @Test
    void testGameStatusTransitions() {
        Game game = new Game();

        game.setStatus(Game.GameStatus.INITIAL);
        assertEquals(Game.GameStatus.INITIAL, game.getStatus());

        game.setStatus(Game.GameStatus.SIGNUP);
        assertEquals(Game.GameStatus.SIGNUP, game.getStatus());

        game.setStatus(Game.GameStatus.ACTIVE);
        assertEquals(Game.GameStatus.ACTIVE, game.getStatus());

        game.setStatus(Game.GameStatus.FINISHED);
        assertEquals(Game.GameStatus.FINISHED, game.getStatus());
    }

    @Test
    void testGameValidation() {
        Game game = new Game();
        game.setMinPlayers(2);
        game.setMaxPlayers(4);

        assertTrue(game.getMinPlayers() <= game.getMaxPlayers());

        game.setMinPlayers(5);
        game.setMaxPlayers(3);

        assertFalse(game.getMinPlayers() <= game.getMaxPlayers());
    }
}