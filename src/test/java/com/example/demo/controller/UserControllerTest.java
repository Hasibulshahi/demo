package com.example.demo.controller;

import com.example.demo.generated.model.UserRequest;
import com.example.demo.generated.model.UserResponse;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Tests")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserRequest userRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        // Create test data
        userRequest = new UserRequest();
        userRequest.setName("John Doe");
        userRequest.setEmail("john@example.com");
        userRequest.setPhone("+60123456789");
        userRequest.setAddress("123 Main St, City, Country");

        userResponse = new UserResponse();
        userResponse.setId(1);
        userResponse.setName("John Doe");
        userResponse.setEmail("john@example.com");
        userResponse.setPhone("+60123456789");
        userResponse.setAddress("123 Main St, City, Country");
    }

    @Test
    @DisplayName("Should create user successfully")
    void testCreateUserSuccess() {
        // Arrange
        when(userService.createUser(any(UserRequest.class)))
                .thenReturn(userResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.createUser(userRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getName());
        assertEquals("john@example.com", response.getBody().getEmail());
        assertEquals(1, response.getBody().getId());

        // Verify
        verify(userService, times(1)).createUser(any(UserRequest.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Should retrieve all users successfully")
    void testGetAllUserSuccess() {
        // Arrange
        List<UserResponse> userList = Arrays.asList(
                userResponse,
                new UserResponse() {{
                    setId(2);
                    setName("Jane Doe");
                    setEmail("jane@example.com");
                    setPhone("+60987654321");
                    setAddress("456 Oak St, City, Country");
                }}
        );
        when(userService.getAllUsers()).thenReturn(userList);

        // Act
        ResponseEntity<List<UserResponse>> response = userController.getAllUser();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).getName());
        assertEquals("Jane Doe", response.getBody().get(1).getName());

        // Verify
        verify(userService, times(1)).getAllUsers();
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Should retrieve all users with empty list")
    void testGetAllUserEmptyList() {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<List<UserResponse>> response = userController.getAllUser();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        // Verify
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void testGetUserByIdSuccess() {
        // Arrange
        Integer userId = 1;
        when(userService.getUserById(userId.longValue()))
                .thenReturn(userResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.getUserById(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getName());
        assertEquals(1, response.getBody().getId());

        // Verify
        verify(userService, times(1)).getUserById(1L);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Should return 404 when user not found by ID")
    void testGetUserByIdNotFound() {
        // Arrange
        Integer userId = 999;
        when(userService.getUserById(userId.longValue())).thenReturn(null);

        // Act
        ResponseEntity<UserResponse> response = userController.getUserById(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        // Verify
        verify(userService, times(1)).getUserById(999L);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUserSuccess() {
        // Arrange
        Integer userId = 1;
        UserRequest updateRequest = new UserRequest();
        updateRequest.setName("John Updated");
        updateRequest.setEmail("john.updated@example.com");
        updateRequest.setPhone("+60111111111");
        updateRequest.setAddress("789 New St, City, Country");

        UserResponse updatedResponse = new UserResponse();
        updatedResponse.setId(1);
        updatedResponse.setName("John Updated");
        updatedResponse.setEmail("john.updated@example.com");
        updatedResponse.setPhone("+60111111111");
        updatedResponse.setAddress("789 New St, City, Country");

        when(userService.updateUser(userId.longValue(), updateRequest))
                .thenReturn(updatedResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.updateUser(userId, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Updated", response.getBody().getName());
        assertEquals("john.updated@example.com", response.getBody().getEmail());
        assertEquals(1, response.getBody().getId());

        // Verify
        verify(userService, times(1)).updateUser(1L, updateRequest);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent user")
    void testUpdateUserNotFound() {
        // Arrange
        Integer userId = 999;
        when(userService.updateUser(userId.longValue(), userRequest)).thenReturn(null);

        // Act
        ResponseEntity<UserResponse> response = userController.updateUser(userId, userRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        // Verify
        verify(userService, times(1)).updateUser(999L, userRequest);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUserSuccess() {
        // Arrange
        Integer userId = 1;
        doNothing().when(userService).deleteUser(userId.longValue());

        // Act
        ResponseEntity<Void> response = userController.deleteUser(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());

        // Verify
        verify(userService, times(1)).deleteUser(1L);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Should handle delete user when service throws exception")
    void testDeleteUserException() {
        // Arrange
        Integer userId = 1;
        doThrow(new RuntimeException("User not found"))
                .when(userService).deleteUser(userId.longValue());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userController.deleteUser(userId));

        // Verify
        verify(userService, times(1)).deleteUser(1L);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Should create user with minimal request data")
    void testCreateUserMinimalData() {
        // Arrange
        UserRequest minimalRequest = new UserRequest();
        minimalRequest.setName("Jane");
        minimalRequest.setEmail("jane@example.com");

        UserResponse minimalResponse = new UserResponse();
        minimalResponse.setId(2);
        minimalResponse.setName("Jane");
        minimalResponse.setEmail("jane@example.com");

        when(userService.createUser(any(UserRequest.class)))
                .thenReturn(minimalResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.createUser(minimalRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getId());
        assertEquals("Jane", response.getBody().getName());

        // Verify
        verify(userService, times(1)).createUser(any(UserRequest.class));
    }
}
