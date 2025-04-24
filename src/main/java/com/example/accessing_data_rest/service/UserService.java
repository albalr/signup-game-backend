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
        return (List<User>) userRepository.findAll();
    }

    public List<User> searchUsers(String name) {
        List<User> user = userRepository.findByName(name);
        return user;
    }

    public User registerUser(User user) {
        if (!userRepository.findByUsername(user.getUsername()).isEmpty()) {
            throw new RuntimeException("Username already exists");
        }
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        List<User> users = userRepository.findByUsername(username);
        if (users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }
} 