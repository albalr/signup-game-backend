package com.example.accessing_data_rest.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void testPlayerCreation() {
        Player player = new Player();
        player.setName("Test Player");
        player.setUid(1L);

        assertEquals("Test Player", player.getName());
        assertEquals(1L, player.getUid());
    }

    @Test
    void testPlayerGameAssociation() {
        Player player = new Player();
        Game game = new Game();
        game.setName("Test Game");

        player.setGame(game);

        assertEquals(game, player.getGame());
        assertEquals("Test Game", player.getGame().getName());
    }

    @Test
    void testPlayerUserAssociation() {
        Player player = new Player();
        User user = new User();
        user.setName("Test User");

        player.setUser(user);

        assertEquals(user, player.getUser());
        assertEquals("Test User", player.getUser().getName());
    }

    @Test
    void testPlayerRelationships() {
        Player player = new Player();
        Game game = new Game();
        User user = new User();

        player.setGame(game);
        player.setUser(user);

        assertNotNull(player.getGame());
        assertNotNull(player.getUser());
    }
}