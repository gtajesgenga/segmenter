package com.example.vtkdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Home redirection to swagger api documentation
 */
@Controller
public class HomeController {

    @GetMapping(value = "/")
    public void index(HttpServletResponse response) throws IOException {
        System.out.println("swagger-ui.html");
        response.sendRedirect("/swagger-ui.html");
    }
}