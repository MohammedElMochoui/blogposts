package com.example.blog_post_manager.post.mapper;

import com.example.blog_post_manager.post.dto.PostDTO;
import com.example.blog_post_manager.post.dto.PostSummaryDTO;
import com.example.blog_post_manager.post.model.Post;

public class PostMapper {
    public static PostSummaryDTO toPostSummaryDto(Post p) {
        return new PostSummaryDTO(p.getTitle(), p.getCreatedAt());
    }

    public static PostDTO toPostDto(Post p) {
        return new PostDTO(p.getTitle(), p.getContent(), p.getCreatedAt());
    }
}
