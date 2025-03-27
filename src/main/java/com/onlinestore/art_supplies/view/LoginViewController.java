package com.onlinestore.art_supplies.view;

import com.onlinestore.art_supplies.dto.LoginRequest;
import com.onlinestore.art_supplies.users.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginViewController {
    private final UserService userService;

    public LoginViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("isAuthenticated", false);
        return "login";
    }

    @PostMapping("/login")
    public String loginPage(@ModelAttribute LoginRequest loginRequest, Model model) {
        try {
            String token = userService.verify(loginRequest);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }
}
