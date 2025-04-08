package com.example.blog_post_manager.user.controller;

import com.example.blog_post_manager.dto.error.ErrorDetails;
import com.example.blog_post_manager.user.dto.*;
import com.example.blog_post_manager.user.model.UserRole;
import com.example.blog_post_manager.user.service.UserManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("users")
public class UserController {
    private final UserManagementService userManagementService;

    public UserController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
        UserResponseDTO userResponseDTO = userManagementService.getUser(username);
        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUsernameById(@PathVariable Long id) {
        UserResponseDTO userResponseDTO = userManagementService.getUser(id);
        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userManagementService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<CreateUserResponseDTO> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        CreateUserResponseDTO createUserResponseDTO = userManagementService.createUser(createUserDTO.username(), createUserDTO.password());
        URI uri = URI.create("/users/" + createUserResponseDTO.id());
        return ResponseEntity.created(uri).body(createUserResponseDTO);
    }

    @PatchMapping
    public ResponseEntity<AddRoleToUserResponseDTO> addRoleToUser(@Valid @RequestBody AddRoleToUserDTO addRoleToUserDTO) {
        AddRoleToUserResponseDTO addRoleToUserResponseDTO = userManagementService.addRoleToUser(addRoleToUserDTO.username(), addRoleToUserDTO.role());
        return ResponseEntity.ok(addRoleToUserResponseDTO);
    }

    @DeleteMapping("/{username}/role/{role}")
    public ResponseEntity<String> deleteRoleFromUser(String username, String role) {
        try {
            UserRole r = UserRole.valueOf(role.toUpperCase());
            userManagementService.removeRoleFromUser(username, r);
        } catch (Exception e) {
            String message = "The role provided is not a valid role!";
            throw new IllegalArgumentException(message);
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userManagementService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        userManagementService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }
}
