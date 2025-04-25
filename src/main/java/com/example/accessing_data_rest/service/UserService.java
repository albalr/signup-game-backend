package com.example.accessing_data_rest.service;

import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

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

    public User registerUser(String name) {
        List<User> existing = userRepository.findByName(name);
        if (!existing.isEmpty()) {
            throw new RuntimeException("User already exists");
        }
        User user = new User();
        user.setName(name);
        return userRepository.save(user);
    }

//    public User signIn(String name) {
//        List<User> users = userRepository.findByName(name);
//        if (!users.isEmpty()) {
//            return users.get(0); // assume first match
//        }
//        return null; // no such user
//    }

} 