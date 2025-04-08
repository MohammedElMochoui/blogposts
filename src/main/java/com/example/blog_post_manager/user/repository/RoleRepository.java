package com.example.blog_post_manager.user.repository;

import com.example.blog_post_manager.user.model.Role;
import com.example.blog_post_manager.user.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(UserRole name);
    boolean existsByName(UserRole name);
}
