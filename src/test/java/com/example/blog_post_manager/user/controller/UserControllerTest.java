package com.example.blog_post_manager.user.controller;

import com.example.blog_post_manager.security.config.SecurityConfig;
import com.example.blog_post_manager.user.dto.*;
import com.example.blog_post_manager.user.model.UserRole;
import com.example.blog_post_manager.user.service.UserManagementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static com.example.blog_post_manager.SecurityConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserManagementService userManagementService;


    @BeforeEach
    void setup() {
    }

    @Test
    @WithMockUser(username = TEST_USER, roles = {USER_ROLE})
    void getUserByUsername_shouldReturnUserWithCorrectUsername() throws Exception {
        final String username = "username";
        final UserResponseDTO userResponseDTO = new UserResponseDTO(username);

        when(userManagementService.getUser(username)).thenReturn(userResponseDTO);

        final MvcResult mvcResult = mockMvc.perform(get("/users/username/" + username)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        final String json = mvcResult.getResponse().getContentAsString();
        final UserResponseDTO result = objectMapper.readValue(json, UserResponseDTO.class);

        verify(userManagementService).getUser(username);
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(username);
    }

    @Test
    @WithMockUser(username = TEST_USER, roles = {USER_ROLE})
    void getUserByUsername_shouldReturnUserWithCorrectUsernameById() throws Exception {
        final Long id = 1L;
        final String username = "username";
        final UserResponseDTO userResponseDTO = new UserResponseDTO(username);

        when(userManagementService.getUser(id)).thenReturn(userResponseDTO);

        final MvcResult mvcResult = mockMvc.perform(get("/users/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        final String json = mvcResult.getResponse().getContentAsString();
        final UserResponseDTO result = objectMapper.readValue(json, UserResponseDTO.class);

        verify(userManagementService).getUser(id);
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(username);
    }

    @Test
    @WithMockUser(username = TEST_USER, roles = {ADMIN_ROLE})
    void getAllUsers_shouldReturnAllUsersThatExist() throws Exception {
        final String username1 = "user1";
        final UserResponseDTO userResponseDTO1 = new UserResponseDTO(username1);
        final UserResponseDTO userResponseDTO2 = new UserResponseDTO(TEST_USER);
        final List<UserResponseDTO> userResponseDTOList = List.of(userResponseDTO1, userResponseDTO2);

        when(userManagementService.getAllUsers()).thenReturn(userResponseDTOList);

        final MvcResult mvcResult = mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        final String json = mvcResult.getResponse().getContentAsString();
        TypeReference<List<UserResponseDTO>> typeReference = new TypeReference<>() {
        };
        final List<UserResponseDTO> result = objectMapper.readValue(json, typeReference);

        verify(userManagementService).getAllUsers();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(userResponseDTO1, userResponseDTO2);
    }

    @Test
    @WithMockUser(username = TEST_USER, roles = {USER_ROLE})
    void getAllUsers_shouldBeForbiddenForDefaultUsers() throws Exception {
        final String username1 = "user1";
        final UserResponseDTO userResponseDTO1 = new UserResponseDTO(username1);
        final UserResponseDTO userResponseDTO2 = new UserResponseDTO(TEST_USER);
        final List<UserResponseDTO> userResponseDTOList = List.of(userResponseDTO1, userResponseDTO2);

        when(userManagementService.getAllUsers()).thenReturn(userResponseDTOList);

        final MvcResult mvcResult = mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andReturn();

        // Should return forbidden before running actual controller method
        verify(userManagementService, never()).getAllUsers();
    }

    @Test
    void createUser_ShouldCreateUserWithCorrectDetailsWithAndWithoutAuth() throws Exception {
        final String username = "user";
        final String password = "password";
        final CreateUserResponseDTO createUserResponseDTO = new CreateUserResponseDTO(1L, username);

        when(userManagementService.createUser(username, password)).thenReturn(createUserResponseDTO);

        final CreateUserDTO body = new CreateUserDTO(username, password);

        final MvcResult mvcResult = mockMvc.perform(post("/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/users/1"))
                .andReturn();

        final String json = mvcResult.getResponse().getContentAsString();
        final CreateUserResponseDTO result = objectMapper.readValue(json, CreateUserResponseDTO.class);

        assertThat(result.username()).isEqualTo(username);
        assertThat(result.id()).isEqualTo(1L);

        final MvcResult mvcResultAuthenticated = mockMvc.perform(post("/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .with(user(TEST_USER).roles(USER_ROLE))
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/users/1"))
                .andReturn();

        final String jsonAuthenticated = mvcResultAuthenticated.getResponse().getContentAsString();
        final CreateUserResponseDTO resultAuthenticated = objectMapper.readValue(jsonAuthenticated, CreateUserResponseDTO.class);

        verify(userManagementService, times(2)).createUser(username, password);
        assertThat(resultAuthenticated.username()).isEqualTo(username);
        assertThat(resultAuthenticated.id()).isEqualTo(1L);
    }

    @Test
    @WithMockUser(username = TEST_USER, roles = {ADMIN_ROLE})
    void addRoleToUser_ShouldAddRoleToUserIfAuthenticatedAsAdmin() throws Exception {
        final String username = "username";
        final UserRole adminRole = UserRole.ADMIN;
        final AddRoleToUserResponseDTO addRoleToUserResponseDTO = new AddRoleToUserResponseDTO(username, adminRole.name());

        when(userManagementService.addRoleToUser(username, adminRole)).thenReturn(addRoleToUserResponseDTO);

        final AddRoleToUserDTO body = new AddRoleToUserDTO(username, adminRole);
        final MvcResult mvcResult = mockMvc.perform(patch("/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                )
                .andExpect(status().isOk())
                .andReturn();

        final String json = mvcResult.getResponse().getContentAsString();
        final AddRoleToUserResponseDTO result = objectMapper.readValue(json, AddRoleToUserResponseDTO.class);

        verify(userManagementService).addRoleToUser(username, adminRole);
        assertThat(result.username()).isEqualTo(username);
        assertThat(result.role()).isEqualTo(adminRole.name());
    }

    @Test
    @WithMockUser(username = TEST_USER, roles = {USER_ROLE})
    void addRoleToUser_ShouldNotAddRoleToUserIfNotAuthenticatedAsAdmin() throws Exception {
        final String username = "username";
        final UserRole adminRole = UserRole.ADMIN;
        final AddRoleToUserResponseDTO addRoleToUserResponseDTO = new AddRoleToUserResponseDTO(username, adminRole.name());

        when(userManagementService.addRoleToUser(username, adminRole)).thenReturn(addRoleToUserResponseDTO);

        final AddRoleToUserDTO body = new AddRoleToUserDTO(username, adminRole);
        final MvcResult mvcResult = mockMvc.perform(patch("/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                )
                .andExpect(status().isForbidden())
                .andReturn();

        verify(userManagementService, never()).addRoleToUser(username, adminRole);
    }
}