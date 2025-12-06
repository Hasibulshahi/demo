package com.example.demo.service;

import com.example.demo.generated.model.UserRequest;
import com.example.demo.generated.model.UserResponse;
import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
}
