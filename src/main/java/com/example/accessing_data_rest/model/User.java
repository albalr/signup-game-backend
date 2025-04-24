package com.example.accessing_data_rest.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="user_table") // this is important! "user" is a keyword in H2 and not an identifier
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.AUTO) // autoincrement; starts at 1602 for some reason
    private long uid;

    @Column(unique = true) // i.e. primary key like in SQL
    private String username;

    private String name;

    // TODO this class needs to be extended with references to Player and
    //      the other way round (similar to the reference from Game to Player
    //      and the other way round. -- done

    @OneToMany(mappedBy = "user") // i.e. one to many relationship in DB
    private List<Player> players;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

}
