package com.example.accessing_data_rest.service;

import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getUsers() {
        return (List<User>) userRepository.findAll(); // gets all users
    }

    public List<User> searchUsers(String name) {
        List<User> users = userRepository.findByName(name);
        for (User u : users) {
            u.setPlayers(null);  // Strip players to avoid nested deserialization
        }
        return users;
    }

    public User registerUser(String name) {
        if (name == null || name.trim().isEmpty()) { // empty username
            throw new IllegalStateException("Username cannot be empty");
        }

        List<User> existing = userRepository.findByName(name);
        if (!existing.isEmpty()) { // user already exists
            throw new IllegalStateException("A user with this name already exists");
        }

        User user = new User(); // finally, saves user
        user.setName(name);
        return userRepository.save(user);
    }

    public User signIn(String name) { 
        if (name == null || name.trim().isEmpty()) { // empty username
            throw new IllegalStateException("Username cannot be empty");
        }

        List<User> users = userRepository.findByName(name);
        if (users.isEmpty()) { // no user found
            throw new IllegalStateException("No user found with this name");
        }
        return users.get(0);
    }

    public void signOut(String name) {
        if (name == null || name.trim().isEmpty()) { // empty username
            throw new IllegalStateException("Username cannot be empty");
        }

        List<User> users = userRepository.findByName(name);
        if (users.isEmpty()) { // no user found
            throw new IllegalStateException("No user found with this name");
        }
    }

} 