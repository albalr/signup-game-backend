package com.example.accessing_data_rest.controller;

import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
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

    @PostMapping(value = "/signup", consumes = "application/json", produces = "application/json")
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user.getName());
    }

//    @GetMapping(value = "/signin", produces = "application/json")
//    public User signIn(@RequestParam("name") String name) {
//        return userService.signIn(name);
//    }






}
