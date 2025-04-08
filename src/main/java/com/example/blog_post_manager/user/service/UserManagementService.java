package com.example.blog_post_manager.user.service;

import com.example.blog_post_manager.post.exception.ResourceNotFoundException;
import com.example.blog_post_manager.user.dto.AddRoleToUserResponseDTO;
import com.example.blog_post_manager.user.dto.CreateUserResponseDTO;
import com.example.blog_post_manager.user.dto.UserResponseDTO;
import com.example.blog_post_manager.user.exception.UserWithUsernameAlreadyExistsException;
import com.example.blog_post_manager.user.mapper.UserMapper;
import com.example.blog_post_manager.user.model.Role;
import com.example.blog_post_manager.user.model.User;
import com.example.blog_post_manager.user.model.UserRole;
import com.example.blog_post_manager.user.repository.RoleRepository;
import com.example.blog_post_manager.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class UserManagementService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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
        Role r = roleRepository.findByName(UserRole.USER)
                .orElseThrow(() -> new IllegalStateException("Default ROLE_USER not found in database!"));
        u.setRoles(Set.of(r));

        User savedUser = userRepository.save(u);
        return UserMapper.toCreateUserResponseDTO(savedUser);
    }

    @Transactional
    public AddRoleToUserResponseDTO addRoleToUser(String username, UserRole role) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        Role r = roleRepository.findByName(role)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + role.name()));

        u.addRole(r);
        return UserMapper.toAddRoleToUserResponseDTO(u, role);
    }

    @Transactional
    public void removeRoleFromUser(String username, UserRole role) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        Role r = roleRepository.findByName(role)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + role.name()));

        u.removeRole(r);
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
