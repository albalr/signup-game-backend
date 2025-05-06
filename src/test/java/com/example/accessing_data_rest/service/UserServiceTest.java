package com.example.accessing_data_rest.service;

import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    /**
     * {@code @Mock} creates a mock object of the specified class.
     * <p>
     * Mock objects are used to simulate the behavior of real objects in a controlled way.
     * They allow us to:
     * <ul>
     *     <li>Define what methods should return when called</li>
     *     <li>Verify that methods were called with specific parameters</li>
     *     <li>Verify the number of times methods were called</li>
     *     <li>Avoid actual database/network calls during testing</li>
     * </ul>
     */
    @Mock
    private UserRepository userRepository;

    /**
     * {@code @InjectMocks} creates an instance of the class and injects the mocks that are created
     * with the {@code @Mock} annotations into this instance.
     * <p>
     * In this case, it creates a UserService instance and automatically injects the
     * mocked UserRepository into it.
     * <p>
     * This allows us to test the UserService in isolation while controlling the behavior
     * of its dependencies through the mocks.
     */
    @InjectMocks
    private UserService userService;

    @Test
    void testGetUsers() {
        // Arrange
        User user1 = new User();
        user1.setName("User1");
        User user2 = new User();
        user2.setName("User2");
        List<User> expectedUsers = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userService.getUsers();

        // Assert
        assertEquals(expectedUsers.size(), actualUsers.size());
        assertEquals(expectedUsers, actualUsers);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testSearchUsers() {
        // Arrange
        String searchName = "Test";
        User user = new User();
        user.setName(searchName);
        List<User> expectedUsers = List.of(user);
        when(userRepository.findByName(searchName)).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userService.searchUsers(searchName);

        // Assert
        assertEquals(expectedUsers.size(), actualUsers.size());
        assertEquals(expectedUsers, actualUsers);
        verify(userRepository, times(1)).findByName(searchName);
    }

    @Test
    void testRegisterUserSuccess() {
        // Arrange
        String userName = "NewUser";
        User savedUser = new User();
        savedUser.setName(userName);
        when(userRepository.findByName(userName)).thenReturn(List.of());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.registerUser(userName);

        // Assert
        assertNotNull(result);
        assertEquals(userName, result.getName());
        verify(userRepository, times(1)).findByName(userName);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUserEmptyName() {
        // Arrange
        String emptyName = "";

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> userService.registerUser(emptyName));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUserAlreadyExists() {
        // Arrange
        String existingName = "ExistingUser";
        User existingUser = new User();
        existingUser.setName(existingName);
        when(userRepository.findByName(existingName)).thenReturn(List.of(existingUser));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> userService.registerUser(existingName));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testSignInSuccess() {
        // Arrange
        String userName = "TestUser";
        User expectedUser = new User();
        expectedUser.setName(userName);
        when(userRepository.findByName(userName)).thenReturn(List.of(expectedUser));

        // Act
        User result = userService.signIn(userName);

        // Assert
        assertNotNull(result);
        assertEquals(userName, result.getName());
        verify(userRepository, times(1)).findByName(userName);
    }

    @Test
    void testSignInEmptyName() {
        // Arrange
        String emptyName = "";

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> userService.signIn(emptyName));
        verify(userRepository, never()).findByName(anyString());
    }

    @Test
    void testSignInUserNotFound() {
        // Arrange
        String nonExistentName = "NonExistentUser";
        when(userRepository.findByName(nonExistentName)).thenReturn(List.of());

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> userService.signIn(nonExistentName));
        verify(userRepository, times(1)).findByName(nonExistentName);
    }

    @Test
    void testUserRegistrationAndAuthentication() {
        // Arrange
        String username = "newuser";
        User newUser = new User();
        newUser.setName(username);

        when(userRepository.findByName(username)).thenReturn(List.of());
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // 1. Register new user
        User registeredUser = userService.registerUser(username);
        assertEquals(username, registeredUser.getName());

        // 2. Try to register same username again
        when(userRepository.findByName(username)).thenReturn(List.of(newUser));
        assertThrows(IllegalStateException.class, () -> userService.registerUser(username));

        // 3. Sign in with valid username
        when(userRepository.findByName(username)).thenReturn(List.of(newUser));
        User signedInUser = userService.signIn(username);
        assertEquals(username, signedInUser.getName());

        // 4. Sign in with invalid username
        assertThrows(IllegalStateException.class, () -> userService.signIn("nonexistentuser"));
    }

    @Test
    void testUserValidation() {
        // Arrange
        String validUsername = "validuser";
        String emptyUsername = "";
        String whitespaceUsername = "   ";

        // 1. Test empty username
        assertThrows(IllegalStateException.class, () -> userService.registerUser(emptyUsername));

        // 2. Test whitespace username
        assertThrows(IllegalStateException.class, () -> userService.registerUser(whitespaceUsername));

        // 3. Test valid username
        User validUser = new User();
        validUser.setName(validUsername);
        when(userRepository.findByName(validUsername)).thenReturn(List.of());
        when(userRepository.save(any(User.class))).thenReturn(validUser);

        User registeredUser = userService.registerUser(validUsername);
        assertEquals(validUsername, registeredUser.getName());
    }

    @Test
    void testUserSearchAndRetrieval() {
        // Arrange
        String searchTerm = "test";
        User user1 = new User();
        user1.setName("testuser1");
        User user2 = new User();
        user2.setName("testuser2");
        User user3 = new User();
        user3.setName("otheruser");

        List<User> allUsers = Arrays.asList(user1, user2, user3);
        List<User> matchingUsers = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(allUsers);
        when(userRepository.findByName(searchTerm)).thenReturn(matchingUsers);

        // 1. Test getting all users
        List<User> retrievedUsers = userService.getUsers();
        assertEquals(3, retrievedUsers.size());

        // 2. Test searching users
        List<User> searchResults = userService.searchUsers(searchTerm);
        assertEquals(2, searchResults.size());
        assertTrue(searchResults.contains(user1));
        assertTrue(searchResults.contains(user2));
        assertFalse(searchResults.contains(user3));
    }

    @Test
    void testUserSessionManagement() {
        // Arrange
        String username = "testuser";
        User user = new User();
        user.setName(username);

        when(userRepository.findByName(username)).thenReturn(List.of(user));

        // 1. Sign in
        User signedInUser = userService.signIn(username);
        assertEquals(username, signedInUser.getName());

        // 2. Sign out
        userService.signOut(username);
    }
}