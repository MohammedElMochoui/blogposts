package com.example.blog_post_manager.post.service;

import com.example.blog_post_manager.post.dto.CreatePostResponseDTO;
import com.example.blog_post_manager.post.dto.PostDTO;
import com.example.blog_post_manager.post.dto.PostSummaryDTO;
import com.example.blog_post_manager.post.exception.ResourceNotFoundException;
import com.example.blog_post_manager.post.model.Post;
import com.example.blog_post_manager.post.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTests {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void findsAllPosts() {
        final List<Post> posts = List.of(
                new Post("title1", "content1"),
                new Post("title2", "content2")
        );
        when(postRepository.findAll()).thenReturn(posts);

        List<PostSummaryDTO> postSummaryDTOs = postService.getAllPostSummary();

        verify(postRepository).findAll();

        assertThat(postSummaryDTOs).isNotNull();
        assertThat(postSummaryDTOs.size()).isEqualTo(2);
        assertThat(postSummaryDTOs.getFirst().title()).isEqualTo("title1");
        assertThat(postSummaryDTOs.get(1).title()).isEqualTo("title2");
    }

    @Test
    void findsAllPostsWhenThereAreNoPosts() {
        when(postRepository.findAll()).thenReturn(List.of());

        List<PostSummaryDTO> postSummaryDTOs = postService.getAllPostSummary();

        assertThat(postSummaryDTOs).isNotNull();
        assertThat(postSummaryDTOs.size()).isEqualTo(0);
    }

    @Test
    void getPost() {
        Post p = new Post("title1", "content1");

        when(postRepository.findById(1L)).thenReturn(Optional.of(p));

        PostDTO postDTO = postService.getPost(1L);

        verify(postRepository).findById(1L);
        assertThat(postDTO.title()).isEqualTo("title1");
        assertThat(postDTO.content()).isEqualTo("content1");
    }

    @Test
    void getPostIfPostDoesNotExist() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            postService.getPost(1L);
        });
        verify(postRepository).findById(1L);
    }

    @Test
    void createPost() {
        String title = "title1";
        String content = "content1";
        LocalDateTime now = LocalDateTime.now();

        Post pWithId = mock();
        when(pWithId.getId()).thenReturn(1L);
        when(pWithId.getTitle()).thenReturn(title);
        when(pWithId.getContent()).thenReturn(content);
        when(pWithId.getCreatedAt()).thenReturn(now);

        when(postRepository.save(any(Post.class))).thenReturn(pWithId);

        CreatePostResponseDTO createPostResponseDTO = postService.createPost(title, content);

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());

        Post p = postCaptor.getValue();
        assertThat(p.getId()).isNull();
        assertThat(p.getTitle()).isEqualTo(title);
        assertThat(p.getContent()).isEqualTo(content);

        assertThat(createPostResponseDTO).isNotNull();
        assertThat(createPostResponseDTO.id()).isEqualTo(1L);
        assertThat(createPostResponseDTO.title()).isEqualTo(title);
        assertThat(createPostResponseDTO.content()).isEqualTo(content);
        assertThat(createPostResponseDTO.createdAt()).isEqualTo(now);
    }

    @Test
    void updatePost() {
        String title = "title1";
        String content = "content1";

        Long id = 1L;
        String updatedTitle = "titleUpdated";
        String updatedContent = "contentUpdated";

        Post p = new Post(title, content);

        when(postRepository.findById(id)).thenReturn(Optional.of(p));

        PostDTO postDTO = postService.updatePost(id, updatedTitle, updatedContent);

        verify(postRepository).findById(id);

        assertThat(postDTO).isNotNull();
        assertThat(postDTO.title()).isEqualTo(updatedTitle);
        assertThat(postDTO.content()).isEqualTo(updatedContent);
    }

    @Test
    void updatePostWhenPostDoesNotExist() {
        Long id = 1L;
        String updatedTitle = "titleUpdated";
        String updatedContent = "contentUpdated";

        when(postRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            postService.updatePost(id, updatedTitle, updatedContent);
        });
        verify(postRepository).findById(id);
    }

    @Test
    void deletePost() {
        Long id = 1L;

        when(postRepository.existsById(id)).thenReturn(true);

        postService.deletePost(id);

        verify(postRepository).existsById(id);
        verify(postRepository).deleteById(id);
    }

    @Test
    void deletePostWhenPostDoesNotExist() {
        Long id = 1L;

        when(postRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            postService.deletePost(id);
        });

        verify(postRepository).existsById(id);
        verify(postRepository, never()).deleteById(id);
    }
}
