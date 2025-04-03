package com.example.blog_post_manager.post.mapper;

import com.example.blog_post_manager.post.dto.CreatePostResponseDTO;
import com.example.blog_post_manager.post.dto.PostDTO;
import com.example.blog_post_manager.post.dto.PostSummaryDTO;
import com.example.blog_post_manager.post.model.Post;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PostMapperTest {


    @Test
    void postToPostSummaryDTO() {
        final Post pWithoutIdAndDate = new Post("title", "content");
        PostSummaryDTO result = PostMapper.toPostSummaryDto(pWithoutIdAndDate);
        assertThat(result.title()).isEqualTo("title");
        assertThat(result.createdAt()).isNull(); // Jpa doesn't set this field in the mapper
    }

    @Test
    void postToPostSummaryDTOWithIdAndDate() {
        final LocalDateTime date = LocalDateTime.of(2022, 1, 1, 1, 1);
        final Post p = new Post("title", "content");
        ReflectionTestUtils.setField(p, "createdAt", date);

        PostSummaryDTO result = PostMapper.toPostSummaryDto(p);
        assertThat(result.title()).isEqualTo("title");
        assertThat(result.createdAt()).isEqualTo(date);
    }

    @Test
    void nullToPostSummaryDTO() {
        PostSummaryDTO p = PostMapper.toPostSummaryDto(null);
        assertThat(p).isNull();
    }

    @Test
    void postToPostDTO() {
        final Post pWithoutIdAndDate = new Post("title", "content");

        PostDTO result = PostMapper.toPostDto(pWithoutIdAndDate);
        assertThat(result.title()).isEqualTo("title");
        assertThat(result.content()).isEqualTo("content");
        assertThat(result.createdAt()).isNull(); // Jpa doesn't set this field in the mapper
    }

    @Test
    void postToPostDTOWithIdAndDate() {
        final LocalDateTime date = LocalDateTime.of(2022, 1, 1, 1, 1);
        final Post p = new Post("title", "content");
        ReflectionTestUtils.setField(p, "createdAt", date);
        ReflectionTestUtils.setField(p, "updatedAt", date);

        PostDTO result = PostMapper.toPostDto(p);
        assertThat(result.title()).isEqualTo("title");
        assertThat(result.content()).isEqualTo("content");
        assertThat(result.createdAt()).isEqualTo(date);
    }

    @Test
    void nullToPostDTO() {
        PostDTO p = PostMapper.toPostDto(null);
        assertThat(p).isNull();
    }

    @Test
    void postToCreatePostResponseDTO() {
        final Post pWithoutIdAndDate = new Post("title", "content");
        CreatePostResponseDTO result = PostMapper.toCreatePostResponseDTO(pWithoutIdAndDate);

        assertThat(result.id()).isNull(); // Jpa doesn't set this field in the mapper
        assertThat(result.title()).isEqualTo("title");
        assertThat(result.content()).isEqualTo("content");
        assertThat(result.createdAt()).isNull(); // Jpa doesn't set this field in the mapper
    }

    @Test
    void postToCreatePostResponseDTOWithIdAndCreatedAt() {
        final LocalDateTime date = LocalDateTime.of(2022, 1, 1, 1, 1);
        final Long id = 1L;
        final Post p = new Post("title", "content");
        ReflectionTestUtils.setField(p, "id", id);
        ReflectionTestUtils.setField(p, "createdAt", date);
        ReflectionTestUtils.setField(p, "updatedAt", date);

        CreatePostResponseDTO result = PostMapper.toCreatePostResponseDTO(p);
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.title()).isEqualTo("title");
        assertThat(result.content()).isEqualTo("content");
        assertThat(result.createdAt()).isEqualTo(date);
    }

    @Test
    void nullToCreatePostResponseDTo() {
        CreatePostResponseDTO p = PostMapper.toCreatePostResponseDTO(null);
        assertThat(p).isNull();
    }
}
