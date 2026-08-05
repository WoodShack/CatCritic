package com.cpan228.catcritic.controller;

import com.cpan228.catcritic.model.LoginForm;
import com.cpan228.catcritic.model.RegisterForm;
import com.cpan228.catcritic.model.User;
import com.cpan228.catcritic.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm form,
                           BindingResult result,
                           HttpServletRequest request) {

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue(
                    "confirmPassword",
                    "error.confirmPassword",
                    "Passwords do not match"
            );
        }

        if (userService.usernameExists(form.getUsername())) {
            result.rejectValue(
                    "username",
                    "error.username",
                    "That username is already taken"
            );
        }

        if (result.hasErrors()) {
            return "register";
        }

        User user = userService.register(
                form.getUsername(),
                form.getPassword(),
                form.getRole()
        );

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);

        request.getSession(true)
                .setAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        context
                );

        return "redirect:/cats";
    }

    @GetMapping("/login")
    public String showLogin(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        model.addAttribute("loginForm", new LoginForm());

        if (error != null) {
            model.addAttribute(
                    "loginError",
                    "Invalid username or password, or your account has been banned."
            );
        }

        if (logout != null) {
            model.addAttribute(
                    "logoutMessage",
                    "You have been logged out."
            );
        }

        return "login";
    }
}
