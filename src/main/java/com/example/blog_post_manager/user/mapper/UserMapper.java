package com.example.blog_post_manager.user.mapper;

import com.example.blog_post_manager.user.dto.CreateUserResponseDTO;
import com.example.blog_post_manager.user.dto.UserResponseDTO;
import com.example.blog_post_manager.user.model.User;

public class UserMapper {
    static public UserResponseDTO toUserResponseDTO(User u) {
        if (u == null) return null;
        return new UserResponseDTO(u.getUsername());
    }

    static public CreateUserResponseDTO toCreateUserResponseDTO(User u) {
        if (u == null) return null;
        return new CreateUserResponseDTO(u.getId(), u.getUsername());
    }

}
