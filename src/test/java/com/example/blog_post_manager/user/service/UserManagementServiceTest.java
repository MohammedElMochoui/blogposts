package com.example.blog_post_manager.user.service;

import com.example.blog_post_manager.post.exception.ResourceNotFoundException;
import com.example.blog_post_manager.user.dto.AddRoleToUserResponseDTO;
import com.example.blog_post_manager.user.dto.CreateUserResponseDTO;
import com.example.blog_post_manager.user.dto.UserResponseDTO;
import com.example.blog_post_manager.user.exception.UserWithUsernameAlreadyExistsException;
import com.example.blog_post_manager.user.model.Role;
import com.example.blog_post_manager.user.model.User;
import com.example.blog_post_manager.user.model.UserRole;
import com.example.blog_post_manager.user.repository.RoleRepository;
import com.example.blog_post_manager.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserManagementService userManagementService;

    @Test
    protected void getUser_returnsCorrectUsername() {
        final Long id = 1L;
        final String username = "username";
        final String password = "password";
        User u = new User(username, password);

        when(userRepository.findById(id)).thenReturn(Optional.of(u));

        UserResponseDTO result = userManagementService.getUser(id);

        verify(userRepository).findById(id);
        verify(userRepository, never()).findByUsername(anyString());
        assertThat(result.username()).isEqualTo(username);
    }

    @Test
    protected void getUser_ThrowsResourceNotFoundExceptionIfUserDoesNotExist() {
        final Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userManagementService.getUser(id));
        verify(userRepository).findById(id);
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    protected void getUser_returnsCorrectUsernameWhenFindingByUsername() {
        final String username = "username";
        final String password = "password";
        User u = new User(username, password);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(u));

        UserResponseDTO result = userManagementService.getUser(username);

        verify(userRepository).findByUsername(username);
        verify(userRepository, never()).findById(anyLong());
        assertThat(result.username()).isEqualTo(username);
    }

    @Test
    protected void getUser_ThrowsResourceNotFoundExceptionIfUserDoesNotExistWhenFindingByUsername() {
        final String username = "username";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userManagementService.getUser(username));
        verify(userRepository).findByUsername(username);
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    protected void getAllUsers_FindsAllUsersAndMapsThemToListOfUserResponseDTO() {
        final String username1 = "username1";
        final String username2 = "username2";
        final String password1 = "password1";
        final String password2 = "password2";
        final User u1 = new User(username1, password1);
        final User u2 = new User(username2, password2);
        final List<User> users = List.of(u1, u2);
        final List<UserResponseDTO> expectedResult = List.of(
                new UserResponseDTO(u1.getUsername()),
                new UserResponseDTO(u2.getUsername())
        );

        when(userRepository.findAll()).thenReturn(users);

        List<UserResponseDTO> userResponseDTOList = userManagementService.getAllUsers();

        verify(userRepository).findAll();
        assertThat(userResponseDTOList).isNotNull();
        assertThat(userResponseDTOList).hasSize(2);
        assertThat(userResponseDTOList).containsAll(expectedResult);
    }

    @Test
    protected void getAllUsers_shouldBeAbleToHandleEmptyLists() {
        final List<User> users = List.of();

        when(userRepository.findAll()).thenReturn(users);

        List<UserResponseDTO> userResponseDTOList = userManagementService.getAllUsers();

        verify(userRepository).findAll();
        assertThat(userResponseDTOList).isNotNull();
        assertThat(userResponseDTOList).hasSize(0);
    }

    @Test
    protected void createUser_ShouldCreateCorrectUserAndSetCorrectRoles() {
        String username = "username";
        String password = "password";
        String hashedPassword = "hashedPassword";
        User savedUser = new User(username, hashedPassword);
        ReflectionTestUtils.setField(savedUser, "id", 1L);

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(roleRepository.findByName(UserRole.USER)).thenReturn(Optional.of(new Role(UserRole.USER)));
        when(passwordEncoder.encode(password)).thenReturn(hashedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        CreateUserResponseDTO result = userManagementService.createUser(username, password);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        // First capture, then get value!
        verify(userRepository).save(userCaptor.capture());
        User user = userCaptor.getValue();

        // Verify mocked methods are all called
        verify(userRepository).existsByUsername(username);
        verify(roleRepository).findByName(UserRole.USER);
        verify(passwordEncoder).encode(password);

        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getPassword()).isEqualTo(hashedPassword);
        assertThat(user.getRoles()).containsExactlyInAnyOrder(new Role(UserRole.USER));

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.username()).isEqualTo(username);
    }

    @Test
    protected void createUser_ShouldThrowExceptionWhenUsernameAlreadyExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(UserWithUsernameAlreadyExistsException.class, () -> userManagementService.createUser("username", "password"));

        verify(roleRepository, never()).findByName(any(UserRole.class));
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    protected void createUser_shouldThrowErrorIfRoleDoesntExistInDatabase() {
        String username = "username";
        String password = "password";
        String hashedPassword = "hashedPassword";

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(hashedPassword);
        when(roleRepository.findByName(UserRole.USER)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> userManagementService.createUser(username, password));

        verify(userRepository).existsByUsername(username);
        verify(passwordEncoder).encode(password);
        verify(roleRepository).findByName(UserRole.USER);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    protected void addRoleToUser_ShouldAddRoleToUser() {
        final String username = "username";
        final String password = "password";
        final UserRole adminUserRole = UserRole.ADMIN;
        final Role adminRole = new Role(adminUserRole);
        final UserRole userUserRole = UserRole.USER;
        final Role userRole = new Role(userUserRole);

        final User user = new User(username, password);
        ReflectionTestUtils.setField(user, "roles", new HashSet<>(List.of(new Role(UserRole.USER))));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(adminUserRole)).thenReturn(Optional.of(adminRole));

        final AddRoleToUserResponseDTO result = userManagementService.addRoleToUser(username, adminUserRole);

        verify(userRepository).findByUsername(username);
        verify(roleRepository).findByName(adminUserRole);

        assertThat(result.username()).isEqualTo(username);
        assertThat(result.role()).isEqualTo(adminRole.getName().name());
        assertThat(user.getRoles()).hasSize(2);
        assertThat(user.getRoles()).contains(adminRole);
    }

    @Test
    protected void addRoleToUser_shouldThrowExceptionIfUserDoesNotExist() {
        final String username = "username";
        final UserRole userRole = UserRole.ADMIN;

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userManagementService.addRoleToUser(username, userRole);
        });

        verify(userRepository).findByUsername(username);
        verify(roleRepository, never()).findByName(userRole);
    }

    @Test
    protected void addRoleToUser_shouldThrowExceptionWhenRoleDoesntExist() {
        // Let's pretend that the role ADMIN has not been added to the database (yet...)
        final UserRole userRole = UserRole.ADMIN;
        final String username = "username";
        final String password = "password";

        final User user = new User(username, password);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(userRole)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userManagementService.addRoleToUser(username, userRole);
        });

        verify(userRepository).findByUsername(username);
        verify(roleRepository).findByName(userRole);
    }

    @Test
    protected void removeRoleFromUser_shouldNotThrowExceptionsOnHappyPath() {
        final String username = "username";
        final UserRole userRole = UserRole.ADMIN;

        User u = new User(username, "");
        Role r = new Role(userRole);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(u));
        when(roleRepository.findByName(userRole)).thenReturn(Optional.of(r));

        assertDoesNotThrow(() -> userManagementService.removeRoleFromUser(username, userRole));

        verify(userRepository).findByUsername(username);
        verify(roleRepository).findByName(userRole);
    }

    @Test
    protected void removeRoleFromUser_shouldThrowExceptionWhenUserNotFound() {
        final String username = "username";
        final UserRole userRole = UserRole.ADMIN;

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userManagementService.removeRoleFromUser(username, userRole));

        verify(userRepository).findByUsername(username);
        verify(roleRepository, never()).findByName(userRole);
    }

    @Test
    protected void removeRoleFromUser_shouldThrowExceptionWhenRoleDoesntExist() {
        final String username = "username";
        final UserRole userRole = UserRole.ADMIN;

        User u = new User(username, "");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(u));
        when(roleRepository.findByName(userRole)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userManagementService.removeRoleFromUser(username, userRole));

        verify(userRepository).findByUsername(username);
        verify(roleRepository).findByName(userRole);
    }

    @Test
    protected void deleteUser_shouldNotThrowAnyExceptionsOnHappyPathWithId() {
        final Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(true);
        assertDoesNotThrow(() -> userManagementService.deleteUser(id));

        verify(userRepository).existsById(id);
        verify(userRepository).deleteById(id);
    }

    @Test
    protected void deleteUser_shouldThrowExceptionIfUserDoesntExistWithId() {
        final Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> userManagementService.deleteUser(id));

        verify(userRepository).existsById(id);
    }

    @Test
    protected void deleteUser_shouldNotThrowAnyExceptionsOnHappyPathWithUsername() {
        final String username = "username";
        when(userRepository.existsByUsername(username)).thenReturn(true);
        assertDoesNotThrow(() -> userManagementService.deleteUser(username));

        verify(userRepository).existsByUsername(username);
        verify(userRepository).deleteByUsername(username);
    }

    @Test
    protected void deleteUser_shouldThrowExceptionIfUserDoesntExistWithUsername() {
        final String username = "username";
        when(userRepository.existsByUsername(username)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> userManagementService.deleteUser(username));

        verify(userRepository).existsByUsername(username);
    }

}