package com.example.accessing_data_rest.controller;

import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("roborally/user") // path to user resource
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/allusers", produces = "application/json")
    public List<User> getAllUsers() {
        return userService.getUsers();
    }
    
    @GetMapping(value = "/searchusers", produces = "application/json")
    public List<User> searchUsers(@RequestParam("name") String name) {
        return userService.searchUsers(name);
    }

    // we use postmapping instead of getmapping because we create and send, and not retrieve
    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @GetMapping("/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }
}
