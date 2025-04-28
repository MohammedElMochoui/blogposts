package com.example.blog_post_manager.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePostDTO(
        @NotBlank(message = "Title cannot be empty!")
        @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters") // Added message to size
        String title,

        @NotBlank(message = "Content cannot be empty!")
        @Size(min = 3, message = "Content must be atleast 3 characters long!") // Added message to size
        String content
) {
}

// Just a small change to test CI/CD