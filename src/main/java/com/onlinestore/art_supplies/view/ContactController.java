package com.onlinestore.art_supplies.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ContactController {
    @GetMapping("/contact")
    public String showContactPage() {
        return "contact";
    }

    @PostMapping("/contact")
    public String handleContactForm(@RequestParam String name, @RequestParam String email, @RequestParam String message) {
        // Handle form submission (e.g., send email, save to database)
        return "redirect:/contact?success";
    }
}
