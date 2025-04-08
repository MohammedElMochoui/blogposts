package com.example.blog_post_manager.user.dto;

import com.example.blog_post_manager.user.model.UserRole;
import jakarta.validation.constraints.NotNull;

public record AddRoleToUserDTO(
        String username,
        @NotNull
        UserRole role
) {
}
