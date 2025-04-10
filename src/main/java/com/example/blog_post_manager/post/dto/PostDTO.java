package com.example.blog_post_manager.post.dto;

import java.time.LocalDateTime;

public record PostDTO(
        String title,
        String content,
        String author,
        LocalDateTime createdAt
) {
}
