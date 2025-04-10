package com.example.blog_post_manager.post.controller;

import com.example.blog_post_manager.post.dto.*;
import com.example.blog_post_manager.post.service.PostService;
import com.example.blog_post_manager.user.model.UserRole;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<List<PostSummaryDTO>> getAll(Authentication auth) {
        List<PostSummaryDTO> posts;
        if (auth.getAuthorities().stream()
                .anyMatch(grantedAuthority ->
                        grantedAuthority.getAuthority().equals("ROLE_" + UserRole.ADMIN.name())
                )) {
            posts = postService.getAllPostSummaryAdmin();
        } else {
            posts = postService.getAllPostSummary(auth.getName());
        }
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id, Principal p) {
        final PostDTO post = postService.getPost(id, p.getName());
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<CreatePostResponseDTO> createPost(@Valid @RequestBody CreatePostDTO createPostDTO, Principal principal) {
        final CreatePostResponseDTO createdPost = postService.createPost(createPostDTO.title(), createPostDTO.content(), principal.getName());
        final URI location = URI.create("/posts/" + createdPost.id());
        return ResponseEntity.created(location).body(createdPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@Valid @RequestBody UpdatePostDTO updatePostDTO, @PathVariable Long id, Principal p) {
        final PostDTO updatedPost = postService.updatePost(id, updatePostDTO.title(), updatePostDTO.content(), p.getName());
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id, Principal p) {
        postService.deletePost(id, p.getName());
        return ResponseEntity.noContent().build();
    }
}
