package com.onlinestore.art_supplies.view;

import com.onlinestore.art_supplies.dto.LoginRequest;
import com.onlinestore.art_supplies.users.User;
import com.onlinestore.art_supplies.users.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginRegisterViewController {
    private final UserService userService;

    public LoginRegisterViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model, @RequestParam(required = false) String error) {
        model.addAttribute("error", error);
        return "login";
    }

    @PostMapping("/login")
    public String loginPage(@ModelAttribute LoginRequest loginRequest, HttpServletResponse response) {
        String jwt = userService.verify(loginRequest);

        if (jwt.equals("fails")) {
            return "redirect:/login?error=Username or password is incorrect";
        }

        Cookie cookie = new Cookie("Authorization", jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1 hour
        response.addCookie(cookie);
        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerPage(@ModelAttribute User user) {
        userService.register(user);
        return "redirect:/login";
    }
}
