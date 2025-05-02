package com.example.accessing_data_rest.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.util.List;

@Entity
@JsonIdentityInfo(
        scope=Game.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "uid")
public class Game {

    @Id
    @Column(name="game_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long uid;

    private String name;

    private int minPlayers;

    private int maxPlayers;

    @OneToMany(mappedBy="game", fetch = FetchType.EAGER)
    private List<Player> players;

    private String owner;

    public enum GameStatus {
        INITIAL,
        SIGNUP,
        ACTIVE,
        FINISHED
    }

    @Enumerated(EnumType.STRING) // for JPA/Hibernate DB stuff
    private GameStatus status = GameStatus.SIGNUP;

    public Game() {
        // Required for Jackson
    }
    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

}
