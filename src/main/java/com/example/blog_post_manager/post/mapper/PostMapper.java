package com.example.blog_post_manager.post.mapper;

import com.example.blog_post_manager.post.dto.CreatePostResponseDTO;
import com.example.blog_post_manager.post.dto.PostDTO;
import com.example.blog_post_manager.post.dto.PostSummaryDTO;
import com.example.blog_post_manager.post.model.Post;

public class PostMapper {
    public static PostSummaryDTO toPostSummaryDto(Post p) {
        if (p == null) return null;
        return new PostSummaryDTO(p.getTitle(), p.getAuthor().getUsername(), p.getCreatedAt());
    }

    public static PostDTO toPostDto(Post p) {
        if (p == null) return null;
        return new PostDTO(p.getTitle(), p.getContent(), p.getAuthor().getUsername(),p.getCreatedAt());
    }

    public static CreatePostResponseDTO toCreatePostResponseDTO(Post p) {
        if (p == null) return null;
        return new CreatePostResponseDTO(p.getId(), p.getTitle(), p.getContent(), p.getAuthor().getUsername(), p.getCreatedAt());
    }
}
