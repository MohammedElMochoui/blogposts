package com.example.blog_post_manager.post.service;

import com.example.blog_post_manager.post.dto.CreatePostResponseDTO;
import com.example.blog_post_manager.post.dto.PostDTO;
import com.example.blog_post_manager.post.dto.PostSummaryDTO;
import com.example.blog_post_manager.post.exception.ResourceNotFoundException;
import com.example.blog_post_manager.post.mapper.PostMapper;
import com.example.blog_post_manager.post.model.Post;
import com.example.blog_post_manager.post.repository.PostRepository;
import com.example.blog_post_manager.user.model.User;
import com.example.blog_post_manager.user.model.UserRole;
import com.example.blog_post_manager.user.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public List<PostSummaryDTO> getAllPostSummary(String username) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find user with username " + username));
        List<Post> posts = postRepository.findByAuthor(u);
        return posts.stream().map(PostMapper::toPostSummaryDto).toList();
    }

    public List<PostSummaryDTO> getAllPostSummaryAdmin() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(PostMapper::toPostSummaryDto).toList();
    }

    public PostDTO getPost(Long id, String username) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find user with username " + username));
        Post p = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find post with id: " + id));
        if (u.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + UserRole.ADMIN))
                && !p.getAuthor().getUsername().equals(u.getUsername()))
            throw new AccessDeniedException("This post does not belong to this author!");
        return PostMapper.toPostDto(p);
    }

    @Transactional
    public CreatePostResponseDTO createPost(String title, String content, String username) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find user with username " + username));
        Post p = new Post(title, content, u);
        Post updated = postRepository.save(p);
        return PostMapper.toCreatePostResponseDTO(updated);
    }

    @Transactional
    public PostDTO updatePost(Long id, String title, String content, String username) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find user with username " + username));
        Post p = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find post with id: " + id));

        if (u.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + UserRole.ADMIN))
                && !p.getAuthor().getUsername().equals(u.getUsername()))
            throw new AccessDeniedException("This post does not belong to this author!");

        p.setTitle(title);
        p.setContent(content);

        return PostMapper.toPostDto(p);
    }

    @Transactional
    public void deletePost(Long id, String username) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find user with username " + username));
        Post p = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find post with id: " + id));
        if (u.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + UserRole.ADMIN))
                && !p.getAuthor().getUsername().equals(u.getUsername()))
            throw new AccessDeniedException("This post does not belong to this author!");
        postRepository.delete(p);
    }
}
