package com.example.blog_post_manager.post.service;

import com.example.blog_post_manager.post.dto.CreatePostResponseDTO;
import com.example.blog_post_manager.post.dto.PostDTO;
import com.example.blog_post_manager.post.dto.PostSummaryDTO;
import com.example.blog_post_manager.post.exception.ResourceNotFoundException;
import com.example.blog_post_manager.post.model.Post;
import com.example.blog_post_manager.post.repository.PostRepository;
import com.example.blog_post_manager.user.model.Role;
import com.example.blog_post_manager.user.model.User;
import com.example.blog_post_manager.user.model.UserRole;
import com.example.blog_post_manager.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.blog_post_manager.SecurityConstants.TEST_USER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTests {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void findsAllPosts() {
        User u = new User("username", "password");
        final List<Post> posts = List.of(
                new Post("title1", "content1", u),
                new Post("title2", "content2", u)
        );
        when(postRepository.findByAuthor(u)).thenReturn(posts);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(u));

        List<PostSummaryDTO> postSummaryDTOs = postService.getAllPostSummary("username");

        verify(postRepository).findByAuthor(u);

        assertThat(postSummaryDTOs).isNotNull();
        assertThat(postSummaryDTOs.size()).isEqualTo(2);
        assertThat(postSummaryDTOs.getFirst().title()).isEqualTo("title1");
        assertThat(postSummaryDTOs.get(1).title()).isEqualTo("title2");
    }

    @Test
    void findsAllPostsWhenThereAreNoPosts() {
        final User u = new User(TEST_USER, "password");
        when(postRepository.findByAuthor(u)).thenReturn(List.of());
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(u));

        List<PostSummaryDTO> postSummaryDTOs = postService.getAllPostSummary("username");

        assertThat(postSummaryDTOs).isNotNull();
        assertThat(postSummaryDTOs.size()).isEqualTo(0);
    }

    @Test
    void getPost() {
        User u = new User(TEST_USER, "password");
        Post p = new Post("title1", "content1", u);

        when(postRepository.findById(1L)).thenReturn(Optional.of(p));
        when(userRepository.findByUsername(TEST_USER)).thenReturn(Optional.of(u));

        PostDTO postDTO = postService.getPost(1L, TEST_USER);

        verify(postRepository).findById(1L);
        assertThat(postDTO.title()).isEqualTo("title1");
        assertThat(postDTO.content()).isEqualTo("content1");
    }

    @Test
    void getPostIfPostDoesNotExist() {
        User u = new User(TEST_USER, "password");
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(TEST_USER)).thenReturn(Optional.of(u));

        assertThrows(ResourceNotFoundException.class, () -> {
            postService.getPost(1L, TEST_USER);
        });
        verify(postRepository).findById(1L);
    }

    @Test
    void getPost_AdminShouldBeAbleToGetSomeoneElsesPost() {
        final User adminUser = new User(TEST_USER, "password");
        adminUser.addRole(new Role(UserRole.ADMIN));
        final User defaultUser = new User("default", "password");
        defaultUser.addRole(new Role(UserRole.USER));
        final Post p = new Post("title1", "content1", defaultUser);

        when(postRepository.findById(1L)).thenReturn(Optional.of(p));
        when(userRepository.findByUsername(TEST_USER)).thenReturn(Optional.of(adminUser));

        PostDTO postDTO = postService.getPost(1L, TEST_USER);

        verify(postRepository).findById(1L);
        assertThat(postDTO.title()).isEqualTo("title1");
        assertThat(postDTO.content()).isEqualTo("content1");
    }

    @Test
    void createPost() {
        final String title = "title1";
        final String content = "content1";
        final LocalDateTime now = LocalDateTime.now();
        final String username = "username";
        final User u = new User(username, "");

        final Post pWithId = mock();
        when(pWithId.getId()).thenReturn(1L);
        when(pWithId.getTitle()).thenReturn(title);
        when(pWithId.getContent()).thenReturn(content);
        when(pWithId.getCreatedAt()).thenReturn(now);
        when(pWithId.getAuthor()).thenReturn(u);

        when(postRepository.save(any(Post.class))).thenReturn(pWithId);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(u));

        final CreatePostResponseDTO createPostResponseDTO = postService.createPost(title, content, username);

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

        User u = new User(TEST_USER, "password");

        Post p = new Post(title, content, u);

        when(postRepository.findById(id)).thenReturn(Optional.of(p));
        when(userRepository.findByUsername(TEST_USER)).thenReturn(Optional.of(u));

        PostDTO postDTO = postService.updatePost(id, updatedTitle, updatedContent, TEST_USER);

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
        User u = new User(TEST_USER, "password");

        when(postRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        when(userRepository.findByUsername(TEST_USER)).thenReturn(Optional.of(u));

        assertThrows(ResourceNotFoundException.class, () -> {
            postService.updatePost(id, updatedTitle, updatedContent, TEST_USER);
        });
        verify(postRepository).findById(id);
    }

    @Test
    void deletePost() {
        final Long id = 1L;
        final User u = new User(TEST_USER, "password");
        final Post p = new Post("title", "content", u);

        when(postRepository.findById(id)).thenReturn(Optional.of(p));
        when(userRepository.findByUsername(TEST_USER)).thenReturn(Optional.of(u));

        postService.deletePost(id, TEST_USER);

        verify(postRepository).findById(id);
        verify(postRepository).delete(p);
    }

    @Test
    void deletePostWhenPostDoesNotExist() {
        final Long id = 1L;
        final User u = new User(TEST_USER, "password");

        when(postRepository.findById(id)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(TEST_USER)).thenReturn(Optional.of(u));

        assertThrows(ResourceNotFoundException.class, () -> {
            postService.deletePost(id, TEST_USER);
        });

        verify(postRepository).findById(id);
        verify(postRepository, never()).deleteById(id);
    }
}
