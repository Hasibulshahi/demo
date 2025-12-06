package com.example.demo.service.impl;

import com.example.demo.entity.UserEntity;
import com.example.demo.generated.model.UserRequest;
import com.example.demo.generated.model.UserResponse;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest userRequest;
    private UserEntity userEntity;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        // Create test user request
        userRequest = new UserRequest();
        userRequest.setName("John Doe");
        userRequest.setEmail("john@example.com");
        userRequest.setPhone("+60123456789");
        userRequest.setAddress("123 Main St, City, Country");

        // Create test user entity
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("John Doe");
        userEntity.setEmail("john@example.com");
        userEntity.setPhone("+60123456789");
        userEntity.setAddress("123 Main St, City, Country");

        // Create test user response
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
        when(userMapper.toEntity(any(UserRequest.class)))
                .thenReturn(userEntity);
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(userEntity);
        when(userMapper.toUserResponse(any(UserEntity.class)))
                .thenReturn(userResponse);

        // Act
        UserResponse response = userService.createUser(userRequest);

        // Assert
        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals(1, response.getId());

        // Verify
        verify(userMapper, times(1)).toEntity(any(UserRequest.class));
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userMapper, times(1)).toUserResponse(any(UserEntity.class));
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Should create user with all fields")
    void testCreateUserWithAllFields() {
        // Arrange
        when(userMapper.toEntity(userRequest)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toUserResponse(userEntity)).thenReturn(userResponse);

        // Act
        UserResponse response = userService.createUser(userRequest);

        // Assert
        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("+60123456789", response.getPhone());
        assertEquals("123 Main St, City, Country", response.getAddress());

        // Verify
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void testGetUserByIdSuccess() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findUserById(userId))
                .thenReturn(userEntity);
        when(userMapper.toUserResponse(userEntity))
                .thenReturn(userResponse);

        // Act
        UserResponse response = userService.getUserById(userId);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("John Doe", response.getName());

        // Verify
        verify(userRepository, times(1)).findUserById(userId);
        verify(userMapper, times(1)).toUserResponse(userEntity);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Should return null when user not found by ID")
    void testGetUserByIdNotFound() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findUserById(userId))
                .thenReturn(null);
        when(userMapper.toUserResponse(null))
                .thenReturn(null);

        // Act
        UserResponse response = userService.getUserById(userId);

        // Assert
        assertNull(response);

        // Verify
        verify(userRepository, times(1)).findUserById(userId);
    }

    @Test
    @DisplayName("Should get all users successfully")
    void testGetAllUsersSuccess() {
        // Arrange
        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setName("Jane Doe");
        user2.setEmail("jane@example.com");
        user2.setPhone("+60987654321");
        user2.setAddress("456 Oak St, City, Country");

        List<UserEntity> userList = Arrays.asList(userEntity, user2);

        UserResponse response2 = new UserResponse();
        response2.setId(2);
        response2.setName("Jane Doe");
        response2.setEmail("jane@example.com");
        response2.setPhone("+60987654321");
        response2.setAddress("456 Oak St, City, Country");

        List<UserResponse> responseList = Arrays.asList(userResponse, response2);

        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.toUsersResponse(userList)).thenReturn(responseList);

        // Act
        List<UserResponse> responses = userService.getAllUsers();

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("John Doe", responses.get(0).getName());
        assertEquals("Jane Doe", responses.get(1).getName());

        // Verify
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toUsersResponse(userList);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Should get all users with empty list")
    void testGetAllUsersEmptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList());
        when(userMapper.toUsersResponse(Arrays.asList())).thenReturn(Arrays.asList());

        // Act
        List<UserResponse> responses = userService.getAllUsers();

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        // Verify
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUserSuccess() {
        // Arrange
        Long userId = 1L;
        UserRequest updateRequest = new UserRequest();
        updateRequest.setName("John Updated");
        updateRequest.setEmail("john.updated@example.com");
        updateRequest.setPhone("+60111111111");
        updateRequest.setAddress("789 New St, City, Country");

        UserEntity existingUser = new UserEntity();
        existingUser.setId(1L);
        existingUser.setName("John Doe");
        existingUser.setEmail("john@example.com");
        existingUser.setPhone("+60123456789");
        existingUser.setAddress("123 Main St, City, Country");

        UserEntity updatedEntity = new UserEntity();
        updatedEntity.setId(1L);
        updatedEntity.setName("John Updated");
        updatedEntity.setEmail("john.updated@example.com");
        updatedEntity.setPhone("+60111111111");
        updatedEntity.setAddress("789 New St, City, Country");

        UserResponse updatedResponse = new UserResponse();
        updatedResponse.setId(1);
        updatedResponse.setName("John Updated");
        updatedResponse.setEmail("john.updated@example.com");
        updatedResponse.setPhone("+60111111111");
        updatedResponse.setAddress("789 New St, City, Country");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(updatedEntity);
        when(userMapper.toUserResponse(any(UserEntity.class)))
                .thenReturn(updatedResponse);

        // Act
        UserResponse response = userService.updateUser(userId, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("John Updated", response.getName());
        assertEquals("john.updated@example.com", response.getEmail());
        assertEquals("+60111111111", response.getPhone());
        assertEquals("789 New St, City, Country", response.getAddress());

        // Verify
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userMapper, times(1)).toUserResponse(any(UserEntity.class));
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void testUpdateUserNotFound() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                userService.updateUser(userId, userRequest));

        // Verify
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should update only changed fields")
    void testUpdateUserPartialUpdate() {
        // Arrange
        Long userId = 1L;
        UserRequest updateRequest = new UserRequest();
        updateRequest.setName("John Updated");
        updateRequest.setEmail("john@example.com"); // same as original
        updateRequest.setPhone("+60111111111"); // changed
        updateRequest.setAddress("123 Main St, City, Country"); // same as original

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(userEntity);
        when(userMapper.toUserResponse(any(UserEntity.class)))
                .thenReturn(userResponse);

        // Act
        UserResponse response = userService.updateUser(userId, updateRequest);

        // Assert
        assertNotNull(response);

        // Verify that save was called with the updated entity
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUserSuccess() {
        // Arrange
        Long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        // Act
        userService.deleteUser(userId);

        // Assert - no exception thrown

        // Verify
        verify(userRepository, times(1)).deleteById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Should handle delete user exception")
    void testDeleteUserException() {
        // Arrange
        Long userId = 1L;
        doThrow(new RuntimeException("Database error"))
                .when(userRepository).deleteById(userId);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                userService.deleteUser(userId));

        // Verify
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("Should create user with minimal data")
    void testCreateUserMinimalData() {
        // Arrange
        UserRequest minimalRequest = new UserRequest();
        minimalRequest.setName("Jane");
        minimalRequest.setEmail("jane@example.com");

        UserEntity minimalEntity = new UserEntity();
        minimalEntity.setId(2L);
        minimalEntity.setName("Jane");
        minimalEntity.setEmail("jane@example.com");

        UserResponse minimalResponse = new UserResponse();
        minimalResponse.setId(2);
        minimalResponse.setName("Jane");
        minimalResponse.setEmail("jane@example.com");

        when(userMapper.toEntity(minimalRequest))
                .thenReturn(minimalEntity);
        when(userRepository.save(minimalEntity))
                .thenReturn(minimalEntity);
        when(userMapper.toUserResponse(minimalEntity))
                .thenReturn(minimalResponse);

        // Act
        UserResponse response = userService.createUser(minimalRequest);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getId());
        assertEquals("Jane", response.getName());

        // Verify
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should handle mapper exception during create")
    void testCreateUserMapperException() {
        // Arrange
        when(userMapper.toEntity(any(UserRequest.class)))
                .thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                userService.createUser(userRequest));

        // Verify
        verify(userMapper, times(1)).toEntity(any(UserRequest.class));
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should handle repository exception during save")
    void testCreateUserRepositoryException() {
        // Arrange
        when(userMapper.toEntity(any(UserRequest.class)))
                .thenReturn(userEntity);
        when(userRepository.save(any(UserEntity.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                userService.createUser(userRequest));

        // Verify
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should get multiple users successfully")
    void testGetAllUsersMultiple() {
        // Arrange
        List<UserEntity> userList = Arrays.asList(
                userEntity,
                createUserEntity(2L, "User 2"),
                createUserEntity(3L, "User 3")
        );

        List<UserResponse> responseList = Arrays.asList(
                userResponse,
                createUserResponse(2, "User 2"),
                createUserResponse(3, "User 3")
        );

        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.toUsersResponse(userList)).thenReturn(responseList);

        // Act
        List<UserResponse> responses = userService.getAllUsers();

        // Assert
        assertEquals(3, responses.size());
        assertEquals("John Doe", responses.get(0).getName());
        assertEquals("User 2", responses.get(1).getName());
        assertEquals("User 3", responses.get(2).getName());

        // Verify
        verify(userRepository, times(1)).findAll();
    }

    /**
     * Helper method to create UserEntity for tests
     */
    private UserEntity createUserEntity(Long id, String name) {
        UserEntity entity = new UserEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setEmail(name.toLowerCase().replace(" ", "") + "@example.com");
        entity.setPhone("+601" + id);
        entity.setAddress("Test Address " + id);
        return entity;
    }

    /**
     * Helper method to create UserResponse for tests
     */
    private UserResponse createUserResponse(Integer id, String name) {
        UserResponse response = new UserResponse();
        response.setId(id);
        response.setName(name);
        response.setEmail(name.toLowerCase().replace(" ", "") + "@example.com");
        response.setPhone("+601" + id);
        response.setAddress("Test Address " + id);
        return response;
    }
}
