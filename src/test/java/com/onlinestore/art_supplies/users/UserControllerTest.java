package com.onlinestore.art_supplies.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ShouldReturnCreatedStatus_WhenUserIsValid() throws Exception {

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userId": 1,
                                    "username": "newUser",
                                    "password": "password",
                                    "fullName": "New User",
                                    "email": "user@mail.com", 
                                    "phone": "1234567890",
                                    "isAdmin": false
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void register_ShouldReturnBadRequestStatus_WhenUserIsInvalid() throws Exception {
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userId": 1,
                                    "username": "",
                                    "password": "",
                                    "fullName": "New User",
                                    "email": "",
                                    "phone": "1234567890",
                                    "isAdmin": false,
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturnOkStatus_WhenCredentialsAreValid() throws Exception {
        User user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user.setFullName("User FullName");

        when(userService.login("user", "password")).thenReturn(user);

        mockMvc.perform(post("/users/login")
                        .param("username", "user")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful! Welcome, " + user.getFullName()));
    }

    @Test
    void login_ShouldReturnUnauthorizedStatus_WhenCredentialsAreInvalid() throws Exception {
        when(userService.login("invalidUser", "invalidPassword")).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post("/users/login")
                        .param("username", "invalidUser")
                        .param("password", "invalidPassword"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password."));
    }

    @Test
    void getAllUsers_ShouldReturnOkStatus_WhenAdminUser() throws Exception {
        User adminUser = new User();
        adminUser.setIsAdmin(true);

        when(userService.getUserById(1L)).thenReturn(Optional.of(adminUser));
        when(userService.getAllUsers()).thenReturn(List.of(new User(), new User()));

        mockMvc.perform(get("/users/all")
                        .param("adminId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllUsers_ShouldReturnForbiddenStatus_WhenNotAdminUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(new User()));

        mockMvc.perform(get("/users/all")
                        .param("adminId", "1"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Access denied: Only admins can view all users."));
    }
}