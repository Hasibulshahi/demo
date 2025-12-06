package com.example.demo.controller;

import com.example.demo.generated.api.UserApi;
import com.example.demo.generated.model.UserRequest;
import com.example.demo.generated.model.UserResponse;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController implements UserApi {

    @Autowired
    private UserService userService;

    @Override
    public ResponseEntity<List<UserResponse>> getAllUser() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Override
    public ResponseEntity<Void> deleteUser(Integer id) {
        userService.deleteUser(Long.valueOf(id));
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<UserResponse> getUserById(Integer id) {
        UserResponse user = userService.getUserById(Long.valueOf(id));
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<UserResponse> updateUser(Integer id, UserRequest request) {
        UserResponse user = userService.updateUser(Long.valueOf(id), request);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<UserResponse> createUser(UserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }
}