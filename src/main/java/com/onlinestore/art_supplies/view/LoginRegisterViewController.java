package com.onlinestore.art_supplies.view;

import com.onlinestore.art_supplies.dto.LoginRequest;
import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginRegisterViewController {
    private final UserService userService;

    public LoginRegisterViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginPage(@ModelAttribute LoginRequest loginRequest, Model model) {
        try {
            String token = userService.verify(loginRequest);
            Boolean isAuthenticated = !token.equals("fails");
            model.addAttribute("isAuthenticated", isAuthenticated);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerPage(@ModelAttribute User user, Model model) {
        userService.register(user);
        return "redirect:/login";
    }

}
