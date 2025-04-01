package com.example.blog_post_manager.post.service;

import com.example.blog_post_manager.post.dto.PostDTO;
import com.example.blog_post_manager.post.dto.PostSummaryDTO;
import com.example.blog_post_manager.post.exception.ResourceNotFoundException;
import com.example.blog_post_manager.post.mapper.PostMapper;
import com.example.blog_post_manager.post.model.Post;
import com.example.blog_post_manager.post.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<PostSummaryDTO> getAllPostSummary() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(PostMapper::toPostSummaryDto).toList();
    }

    public PostDTO getPost(Long id) {
        Post p = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find post with id: " + id));
        return PostMapper.toPostDto(p);
    }

    public PostDTO createPost(String title, String content) {
        Post p = new Post(title, content);
        Post updated = postRepository.save(p);
        return PostMapper.toPostDto(updated);
    }

    public PostDTO updatePost(Long id, String title, String content) {
        Post p = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find post with id: " + id));

        p.setTitle(title);
        p.setContent(content);

        Post updated = postRepository.save(p);
        return PostMapper.toPostDto(updated);
    }

    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) throw new ResourceNotFoundException("Cannot find post with id: " + id);
        postRepository.deleteById(id);
    }
}
