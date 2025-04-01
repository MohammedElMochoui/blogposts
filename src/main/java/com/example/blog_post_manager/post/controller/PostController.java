package com.example.blog_post_manager.post.controller;

import com.example.blog_post_manager.post.dto.CreatePostDTO;
import com.example.blog_post_manager.post.dto.PostDTO;
import com.example.blog_post_manager.post.dto.PostSummaryDTO;
import com.example.blog_post_manager.post.dto.UpdatePostDTO;
import com.example.blog_post_manager.post.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<PostSummaryDTO> getAll() {
        return postService.getAllPostSummary();
    }

    @GetMapping("/{id}")
    public PostDTO getAll(@PathVariable Long id) {
        return postService.getPost(id);
    }

    @PostMapping
    public PostDTO createPost(@RequestBody CreatePostDTO createPostDTO) {
        return postService.createPost(createPostDTO.title(), createPostDTO.content());
    }

    @PutMapping("/{id}")
    public PostDTO updatePost(@RequestBody UpdatePostDTO updatePostDTO, @PathVariable Long id) {
        return postService.updatePost(id, updatePostDTO.title(), updatePostDTO.content());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@RequestBody UpdatePostDTO updatePostDTO, @PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok("Post with id " + id + " deleted sucessfully");
    }
}
