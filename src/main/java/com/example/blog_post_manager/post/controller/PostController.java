package com.example.blog_post_manager.post.controller;

import com.example.blog_post_manager.post.dto.*;
import com.example.blog_post_manager.post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostSummaryDTO>> getAll() {
        final List<PostSummaryDTO> posts = postService.getAllPostSummary();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        final PostDTO post = postService.getPost(id);
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<CreatePostResponseDTO> createPost(@Valid @RequestBody CreatePostDTO createPostDTO, Principal principal) {
        final CreatePostResponseDTO createdPost = postService.createPost(createPostDTO.title(), createPostDTO.content(), principal.getName());
        final URI location = URI.create("/posts/" + createdPost.id());
        return ResponseEntity.created(location).body(createdPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@Valid @RequestBody UpdatePostDTO updatePostDTO, @PathVariable Long id) {
        final PostDTO updatedPost = postService.updatePost(id, updatePostDTO.title(), updatePostDTO.content());
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
