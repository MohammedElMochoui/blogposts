package com.example.blog_post_manager.user.exception;

public class UserWithUsernameAlreadyExistsException extends RuntimeException {
    public UserWithUsernameAlreadyExistsException(String username) {
        super("User with username (" + username + ") already exists!");
    }
}
