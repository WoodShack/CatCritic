package com.cpan228.catcritic.controller;

import com.cpan228.catcritic.service.CatService;
import com.cpan228.catcritic.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final CatService catService;

    public AdminController(UserService userService, CatService catService) {
        this.userService = userService;
        this.catService = catService;
    }

    @GetMapping
    public String dashboard(Model model, Authentication authentication) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("cats", catService.findAllCats());
        model.addAttribute("currentUsername", authentication.getName());
        return "admin";
    }

    @PostMapping("/users/{id}/ban")
    public String banUser(@PathVariable Long id, Authentication authentication) {
        userService.findById(id).ifPresent(user -> {
            if (!user.getUsername().equalsIgnoreCase(authentication.getName())) {
                userService.setBanned(id, true);
            }
        });
        return "redirect:/admin";
    }

    @PostMapping("/users/{id}/unban")
    public String unbanUser(@PathVariable Long id) {
        userService.setBanned(id, false);
        return "redirect:/admin";
    }

    @PostMapping("/cats/{id}/delete")
    public String deleteCat(@PathVariable Long id) {
        catService.deleteCat(id);
        return "redirect:/admin";
    }
}
