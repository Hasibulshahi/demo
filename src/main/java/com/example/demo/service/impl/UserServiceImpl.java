package com.example.demo.service.impl;

import com.example.demo.entity.UserEntity;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import com.example.demo.generated.model.UserRequest;
import com.example.demo.generated.model.UserResponse;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper mapper;

    @Override
    public UserResponse createUser(UserRequest request) {
        UserEntity userEntity = mapper.toEntity(request);
        userRepository.save(userEntity);
        return mapper.toUserResponse(userEntity);
    }

    @Override
    public UserResponse getUserById(Long id) {
        UserEntity userEntity = userRepository.findUserById(id);
        return mapper.toUserResponse(userEntity);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return mapper.toUsersResponse(userRepository.findAll());
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        existingUser.setName(request.getName());
        existingUser.setPhone(request.getPhone());
        existingUser.setEmail(request.getEmail());
        existingUser.setAddress(request.getAddress());
        userRepository.save(existingUser);
        return mapper.toUserResponse(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

