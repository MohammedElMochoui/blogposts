package com.example.blog_post_manager.post.repository;

import com.example.blog_post_manager.post.model.Post;
import com.example.blog_post_manager.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthor(User author);
}
