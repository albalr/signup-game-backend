package com.example.accessing_data_rest.controller;

import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // GET
    @GetMapping(value = "/allusers", produces = "application/json")
    public List<User> getAllUsers() {
        return userService.getUsers();
    }
    
    @GetMapping(value = "/searchusers", produces = "application/json")
    public List<User> searchUsers(@RequestParam("name") String name) {
        return userService.searchUsers(name);
    }

    // POST
    @PostMapping(value = "/signup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user.getName());
            return ResponseEntity.ok(registeredUser);
        } catch (IllegalStateException e) { // for expected errors etc etc
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {             // for unexpected errors etc etc
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "An unexpected error occurred while registering user"));
        }
    }

    @PostMapping(value = "/signin", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> signIn(@RequestBody User user) {
        try {
            User signedInUser = userService.signIn(user.getName());
            return ResponseEntity.ok(signedInUser);
        } catch (IllegalStateException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "An unexpected error occurred while signing in"));
        }
    }

    @PostMapping(value = "/signout", consumes = "application/json")
    public ResponseEntity<?> signOut(@RequestBody User user) {
        try {
            userService.signOut(user.getName());
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "An unexpected error occurred while signing out"));
        }
    }
}
