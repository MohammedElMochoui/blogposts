package com.example.blog_post_manager.post.dto;

import java.time.LocalDateTime;

public record PostSummaryDTO(
        String title,
        String author,
        LocalDateTime createdAt
) {
}
