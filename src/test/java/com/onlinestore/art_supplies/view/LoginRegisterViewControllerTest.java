package com.onlinestore.art_supplies.view;

import com.onlinestore.art_supplies.config.security.CustomAuthenticationFilter;
import com.onlinestore.art_supplies.dto.LoginRequest;
import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("h2")
@WebMvcTest(LoginRegisterViewController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoginRegisterViewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    CustomAuthenticationFilter customAuthenticationFilter;

    @Test
    public void testShowLoginPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));

    }

    @Test
    public void testShowLoginPageWithError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login").param("error", "Wrong credentials"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    public void testLoginPageSuccess() throws Exception {
        when(userService.verify(any(LoginRequest.class))).thenReturn("valid.jwt.token");

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .param("username", "user")
                        .param("password", "pass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(cookie().exists("Authorization"));
    }

    @Test
    public void testLoginPageFailure() throws Exception {
        when(userService.verify(any(LoginRequest.class))).thenReturn("fails");

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .param("username", "wronguser")
                        .param("password", "wrongpass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=Username or password is incorrect"));
    }

    @Test
    public void testShowRegisterPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void testRegisterPage() throws Exception {
        User dummyUser = new User();
        when(userService.register(any())).thenReturn(dummyUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .param("username", "john")
                        .param("password", "123456")
                        .param("email", "john@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService, times(1)).register(any(User.class));
    }

}