package com.example.blog_post_manager.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserDTO(
        @NotBlank(message = "Username cannot be empty.")
        @Size(min = 4, max = 255, message = "Username has to be between 4 and 255 characters long!")
        String username,
        @Size(min = 8, message = "Password has to be atleast 8 characters long!")
        @NotBlank(message = "Password cannot be empty.")
        String password
) {
}
