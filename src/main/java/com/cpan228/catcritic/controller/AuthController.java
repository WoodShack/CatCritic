package com.cpan228.catcritic.controller;

import com.cpan228.catcritic.model.LoginForm;
import com.cpan228.catcritic.model.RegisterForm;
import com.cpan228.catcritic.model.User;
import com.cpan228.catcritic.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
public class AuthController {

    public static final String SESSION_USERNAME = "username";

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
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm form, BindingResult result,
                            HttpSession session) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
        }
        if (userService.usernameExists(form.getUsername())) {
            result.rejectValue("username", "error.username", "That username is already taken");
        }
        if (result.hasErrors()) {
            return "register";
        }
        User user = userService.register(form.getUsername(), form.getPassword());
        session.setAttribute(SESSION_USERNAME, user.getUsername());
        return "redirect:/cats";
    }

    @GetMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginForm form, BindingResult result,
                         HttpSession session, Model model) {
        if (result.hasErrors()) {
            return "login";
        }
        Optional<User> user = userService.authenticate(form.getUsername(), form.getPassword());
        if (user.isEmpty()) {
            model.addAttribute("loginError", "Invalid username or password");
            return "login";
        }
        session.setAttribute(SESSION_USERNAME, user.get().getUsername());
        return "redirect:/cats";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
