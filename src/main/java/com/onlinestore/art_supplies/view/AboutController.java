package com.onlinestore.art_supplies.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutController {
    @GetMapping("/about")
    public String showAboutPage() {
        return "about";
    }
}
