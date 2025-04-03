package com.example.blog_post_manager.user.service;

import com.example.blog_post_manager.post.exception.ResourceNotFoundException;
import com.example.blog_post_manager.user.dto.CreateUserResponseDTO;
import com.example.blog_post_manager.user.dto.UserResponseDTO;
import com.example.blog_post_manager.user.exception.UserWithUsernameAlreadyExistsException;
import com.example.blog_post_manager.user.mapper.UserMapper;
import com.example.blog_post_manager.user.model.User;
import com.example.blog_post_manager.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserManagementService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO getUser(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id (" + id + ") not found!"));
        return UserMapper.toUserResponseDTO(u);
    }

    public UserResponseDTO getUser(String username) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username (" + username + ") not found!"));
        return UserMapper.toUserResponseDTO(u);
    }

    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserMapper::toUserResponseDTO).toList();
    }

    @Transactional
    public CreateUserResponseDTO createUser(String username, String password) {
        if (userRepository.existsByUsername(username))
            throw new UserWithUsernameAlreadyExistsException(username);

        User u = new User(username, passwordEncoder.encode(password));
        User savedUser = userRepository.save(u);
        return UserMapper.toCreateUserResponseDTO(savedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id))
            throw new ResourceNotFoundException("User with id (" + id + ") not found!");
        userRepository.deleteById(id);
    }

    @Transactional
    public void deleteUser(String username) {
        if (!userRepository.existsByUsername(username))
            throw new ResourceNotFoundException("User with username (" + username + ") not found!");
        userRepository.deleteByUsername(username);
    }
}
