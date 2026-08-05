package com.cpan228.catcritic.controller;

import com.cpan228.catcritic.model.Cat;
import com.cpan228.catcritic.model.Role;
import com.cpan228.catcritic.model.User;
import com.cpan228.catcritic.service.CatService;
import com.cpan228.catcritic.service.UserService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final CatService catService;

    public AdminController(UserService userService, CatService catService) {
        this.userService = userService;
        this.catService = catService;
    }


    @GetMapping
    public String dashboard(Model model,
                            @AuthenticationPrincipal User currentUser) {

        model.addAttribute("users", userService.findAll());

        model.addAttribute(
                "cats",
                catService.browse(
                        null,
                        null,
                        PageRequest.of(
                                0,
                                500,
                                Sort.by("createdAt").descending()
                        )
                ).getContent()
        );

        model.addAttribute("roles", Role.values());
        model.addAttribute("currentUser", currentUser);

        return "admin/dashboard";
    }


    @PostMapping("/users/{id}/role")
    public String changeRole(@PathVariable Long id,
                             @RequestParam Role role) {

        userService.changeRole(id, role);

        return "redirect:/admin";
    }


    @PostMapping("/users/{id}/delete")
    public String deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {


        if (currentUser != null &&
                currentUser.getId().equals(id)) {

            return "redirect:/admin?selfDeleteBlocked";
        }


        userService.deleteUser(id);

        return "redirect:/admin";
    }


    @PostMapping("/cats/{id}/delete")
    public String deleteCat(@PathVariable Long id) {

        Cat cat = catService.getCatById(id);

        if (cat != null) {
            catService.deleteCat(id);
        }

        return "redirect:/admin";
    }
}