package com.example.vtkdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/ui")
    public String home() {
        return "ui/index";
    }

    @GetMapping(value = {"/"})
    public String swagger() {
        return "redirect:/swagger-ui/index.html";
    }
}
