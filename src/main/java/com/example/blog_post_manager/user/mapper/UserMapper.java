package com.example.blog_post_manager.user.mapper;

import com.example.blog_post_manager.user.dto.AddRoleToUserResponseDTO;
import com.example.blog_post_manager.user.dto.CreateUserResponseDTO;
import com.example.blog_post_manager.user.dto.UserResponseDTO;
import com.example.blog_post_manager.user.model.User;
import com.example.blog_post_manager.user.model.UserRole;

public class UserMapper {
    static public UserResponseDTO toUserResponseDTO(User u) {
        if (u == null) return null;
        return new UserResponseDTO(u.getUsername());
    }

    static public CreateUserResponseDTO toCreateUserResponseDTO(User u) {
        if (u == null) return null;
        return new CreateUserResponseDTO(u.getId(), u.getUsername());
    }

    static public AddRoleToUserResponseDTO toAddRoleToUserResponseDTO(User u, UserRole r) {
        if (u == null) return null;
        return new AddRoleToUserResponseDTO(u.getUsername(), r.name());
    }

}
