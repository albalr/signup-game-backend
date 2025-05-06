package com.example.accessing_data_rest.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

class UserTest {

    @Test
    void testUserCreation() {
        User user = new User();
        user.setName("testuser");
        user.setUid(1);

        assertEquals("testuser", user.getName());
        assertEquals(1, user.getUid());
    }

    @Test
    void testUserPlayers() {
        User user = new User();
        List<Player> players = new ArrayList<>();
        Player player = new Player();
        players.add(player);
        user.setPlayers(players);

        assertEquals(1, user.getPlayers().size());
        assertEquals(player, user.getPlayers().get(0));
    }
}