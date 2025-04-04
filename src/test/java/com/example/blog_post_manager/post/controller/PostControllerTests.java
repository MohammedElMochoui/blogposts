package com.example.blog_post_manager.post.controller;

import com.example.blog_post_manager.dto.error.ErrorDetails;
import com.example.blog_post_manager.post.dto.*;
import com.example.blog_post_manager.post.exception.ResourceNotFoundException;
import com.example.blog_post_manager.post.service.PostService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
public class PostControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    PostService postService;

    @BeforeEach
    void setup() {
    }

    @Test
    void getAllPosts() throws Exception {
        final String title1 = "title1";
        final String title2 = "title2";
        final String title3 = "title3";
        final LocalDateTime t = LocalDateTime.of(2025, 1, 1, 1, 1);
        final LocalDateTime t2 = LocalDateTime.of(2025, 1, 1, 1, 1);
        final LocalDateTime t3 = LocalDateTime.of(2025, 1, 1, 1, 1);

        when(postService.getAllPostSummary()).thenReturn(List.of(
                new PostSummaryDTO(title1, t),
                new PostSummaryDTO(title2, t2),
                new PostSummaryDTO(title3, t3)
        ));

        final MvcResult result = mockMvc.perform(get("/posts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final String json = result.getResponse().getContentAsString();
        final TypeReference<List<PostSummaryDTO>> postSummaryListType = new TypeReference<>() {
        };
        final List<PostSummaryDTO> posts = objectMapper.readValue(json, postSummaryListType);

        verify(postService).getAllPostSummary();

        assertThat(posts).isNotNull();
        assertThat(posts.size()).isEqualTo(3);
        assertThat(posts.getFirst().title()).isEqualTo(title1);
        assertThat(posts.get(1).title()).isEqualTo(title2);
        assertThat(posts.getLast().title()).isEqualTo(title3);
    }

    @Test
    void getPostById() throws Exception {
        final String title = "title1";
        final String content = "content1";
        final LocalDateTime t = LocalDateTime.of(2025, 1, 1, 1, 1);
        final PostDTO postDTO = new PostDTO(title, content, t);

        final Long id = 1L;
        when(postService.getPost(id)).thenReturn(postDTO);

        MvcResult result = mockMvc.perform(get("/posts/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        PostDTO resultPost = objectMapper.readValue(json, PostDTO.class);

        verify(postService).getPost(id);

        assertThat(resultPost).isNotNull();
        assertThat(resultPost.title()).isEqualTo(title);
        assertThat(resultPost.content()).isEqualTo(content);
        assertThat(resultPost.createdAt()).isEqualTo(t);
    }

    @Test
    void getPostByIdThatDoesntExist() throws Exception {
        final Long id = 1L;
        final String errorMessage = "Cannot find post with id: " + id;
        final String url = "/posts/1";
        when(postService.getPost(id)).thenThrow(new ResourceNotFoundException(errorMessage));

        MvcResult result = mockMvc.perform(get(url)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ErrorDetails error = objectMapper.readValue(json, ErrorDetails.class);

        verify(postService).getPost(id);
        assertThat(error.statuscode()).isEqualTo(404);
        assertThat(error.message()).isEqualTo(errorMessage);
        assertThat(error.details()).isEqualTo("uri=" + url);
    }

    @Test
    void createPost() throws Exception {
        final Long id = 1L;
        final String title = "title";
        final String content = "content";
        final LocalDateTime date = LocalDateTime.of(2025, 1, 1, 1, 1);

        final CreatePostDTO createPostDTO = new CreatePostDTO(title, content);

        final CreatePostResponseDTO createPostResponseDTO = new CreatePostResponseDTO(id, title, content, date);
        when(postService.createPost(title, content)).thenReturn(createPostResponseDTO);

        MvcResult mvcResult = mockMvc.perform(post("/posts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPostDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/posts/" + id))
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        CreatePostResponseDTO responseDto = objectMapper.readValue(json, CreatePostResponseDTO.class);

        verify(postService).createPost(title, content);
        assertThat(responseDto.id()).isEqualTo(id);
        assertThat(responseDto.title()).isEqualTo(title);
        assertThat(responseDto.content()).isEqualTo(content);
        assertThat(responseDto.createdAt()).isEqualTo(date);
    }

    @Test
    void createPostWithInvalidInput() throws Exception {
        final Long id = 1L;
        final String title = ""; // cannot be empty
        final String content = "a"; // has to be atleast 3 characters

        final CreatePostDTO createPostDTO = new CreatePostDTO(title, content);

        MvcResult mvcResult = mockMvc.perform(post("/posts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPostDTO)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        TypeReference<Map<String, String>> typeReference = new TypeReference<>() {
        };
        Map<String, String> errors = objectMapper.readValue(json, typeReference);

        verify(postService, never()).createPost(title, content); // Verify that code didn't reach this far!
        assertThat(errors.size()).isEqualTo(2);
        assertThat(errors.get("title")).isNotBlank();
        assertThat(errors.get("content")).isNotBlank();
    }

    @Test
    void updatePost() throws Exception {
        final Long id = 1L;
        final String newTitle = "new title";
        final String newContent = "new content";
        final LocalDateTime date = LocalDateTime.of(2025, 1, 1, 1, 1);
        final UpdatePostDTO updatePostDTO = new UpdatePostDTO(newTitle, newContent);

        final PostDTO postDTO = new PostDTO(newTitle, newContent, date);
        when(postService.updatePost(id, newTitle, newContent)).thenReturn(postDTO);

        MvcResult mvcResult = mockMvc.perform(put("/posts/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePostDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        PostDTO result = objectMapper.readValue(json, PostDTO.class);

        verify(postService).updatePost(id, newTitle, newContent);
        assertThat(result.title()).isEqualTo(newTitle);
        assertThat(result.content()).isEqualTo(newContent);
        assertThat(result.createdAt()).isEqualTo(date);
    }

    @Test
    void updatePostWhenInputIsInvalid() throws Exception {
        final Long id = 1L;
        final String newTitle = ""; // cannot be empty
        final String newContent = "a"; // has to be atleast 3 characters
        final UpdatePostDTO updatePostDTO = new UpdatePostDTO(newTitle, newContent);

        MvcResult mvcResult = mockMvc.perform(put("/posts/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePostDTO)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        TypeReference<Map<String, String>> typeReference = new TypeReference<>() {
        };
        Map<String, String> errors = objectMapper.readValue(json, typeReference);

        verify(postService, never()).updatePost(id, newTitle, newContent);
        assertThat(errors.size()).isEqualTo(2);
        assertThat(errors.get("title")).isNotBlank();
        assertThat(errors.get("content")).isNotBlank();
    }

    @Test
    void deletePost() throws Exception {
        final Long id = 1L;
        doNothing().when(postService).deletePost(id);

        MvcResult mvcResult = mockMvc.perform(delete("/posts/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(postService).deletePost(id);
    }


    @Test
    void deletePostThatDoesntExist() throws Exception {
        final Long id = 1L;
        final String url = "/posts/" + id;
        final String errorMessage = "Cannot find post with id: " + id;
        doThrow(new ResourceNotFoundException(errorMessage)).when(postService).deletePost(id);

        MvcResult mvcResult = mockMvc.perform(delete(url)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        ErrorDetails errors = objectMapper.readValue(json, ErrorDetails.class);

        verify(postService).deletePost(id);
        assertThat(errors.statuscode()).isEqualTo(404);
        assertThat(errors.message()).isEqualTo(errorMessage);
        assertThat(errors.details()).isEqualTo("uri=" + url);
    }
}