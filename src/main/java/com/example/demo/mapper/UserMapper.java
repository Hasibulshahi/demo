package com.example.demo.mapper;

import com.example.demo.entity.UserEntity;
import com.example.demo.generated.model.UserRequest;
import com.example.demo.generated.model.UserResponse;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toEntity(UserRequest user);
    UserResponse toUserResponse(UserEntity user);
    List<UserResponse> toUsersResponse(List<UserEntity> user);
}
