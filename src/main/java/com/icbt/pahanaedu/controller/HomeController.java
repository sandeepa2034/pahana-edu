package com.icbt.pahanaedu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("appName", "Pahana Edu Bookshop");
        model.addAttribute("message", "Welcome to Colombo's Premier Bookshop");
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("appName", "Pahana Edu Bookshop - Admin");
        return "dashboard";
    }

}
