package com.cpan228.catcritic.controller;

import com.cpan228.catcritic.model.RegisterForm;
import com.cpan228.catcritic.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm form, BindingResult result) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
        }
        if (userService.usernameExists(form.getUsername())) {
            result.rejectValue("username", "error.username", "That username is already taken");
        }
        if (result.hasErrors()) {
            return "register";
        }
        userService.register(form.getUsername(), form.getPassword());
        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String showLogin(Model model) {
        return "login";
    }
}
