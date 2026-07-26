package com.cpan228.catcritic.controller;

import com.cpan228.catcritic.model.Breed;
import com.cpan228.catcritic.model.Cat;
import com.cpan228.catcritic.service.CatService;
import com.cpan228.catcritic.service.RatingService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Controller
public class CatController {

    private static final Set<String> SORTABLE_FIELDS = Set.of("age", "breed", "createdAt");

    private final CatService catService;
    private final RatingService ratingService;

    public CatController(CatService catService, RatingService ratingService) {
        this.catService = catService;
        this.ratingService = ratingService;
    }

    @GetMapping("/cats/new")
    public String showSubmitForm(Model model, HttpSession session) {
        String username = (String) session.getAttribute(AuthController.SESSION_USERNAME);
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("cat", new Cat());
        model.addAttribute("breeds", Breed.values());
        return "cat-form";
    }

    @PostMapping("/cats/new")
    public String submitCat(@Valid @ModelAttribute("cat") Cat cat, BindingResult result,
                             @RequestParam("photo") MultipartFile photo, Model model, HttpSession session) {
        String username = (String) session.getAttribute(AuthController.SESSION_USERNAME);
        if (username == null) {
            return "redirect:/login";
        }

        if (photo.isEmpty()) {
            result.reject("photo.required", "A photo of your cat is required");
        } else if (photo.getContentType() == null || !photo.getContentType().startsWith("image/")) {
            result.reject("photo.invalid", "The uploaded file must be an image");
        }

        if (result.hasErrors()) {
            model.addAttribute("breeds", Breed.values());
            return "cat-form";
        }

        try {
            Cat saved = catService.saveCat(cat, photo, username);
            return "redirect:/cats/" + saved.getId();
        } catch (IOException e) {
            model.addAttribute("breeds", Breed.values());
            model.addAttribute("uploadError", "We couldn't save that photo. Please try again.");
            return "cat-form";
        }
    }

    @GetMapping("/cats/{id}")
    public String showCat(@PathVariable Long id, Model model) {
        Cat cat = catService.getCatById(id);
        if (cat == null) {
            return "redirect:/cats";
        }
        model.addAttribute("cat", cat);
        return "cat-detail";
    }

    @GetMapping("/cats")
    public String browse(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) Integer minAge,
            Model model, HttpSession session) {

        String sortField = SORTABLE_FIELDS.contains(sort) ? sort : "createdAt";
        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException e) {
            sortDirection = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));

        Breed breedFilter = null;
        if (breed != null && !breed.isBlank()) {
            try {
                breedFilter = Breed.valueOf(breed);
            } catch (IllegalArgumentException ignored) {
            }
        }

        Page<Cat> catPage = catService.browse(breedFilter, minAge, pageable);

        model.addAttribute("cats", catPage.getContent());
        model.addAttribute("totalPages", catPage.getTotalPages());
        model.addAttribute("totalElements", catPage.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("hasPrevious", catPage.hasPrevious());
        model.addAttribute("hasNext", catPage.hasNext());
        model.addAttribute("sort", sortField);
        model.addAttribute("direction", sortDirection.name());
        model.addAttribute("breed", breed);
        model.addAttribute("minAge", minAge);
        model.addAttribute("breeds", Breed.values());
        model.addAttribute("loggedIn", session.getAttribute(AuthController.SESSION_USERNAME) != null);

        return "browse";
    }

    @PostMapping("/cats/{id}/rate")
    public String rateCat(@PathVariable Long id, @RequestParam int stars,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "6") int size,
                           @RequestParam(defaultValue = "createdAt") String sort,
                           @RequestParam(defaultValue = "DESC") String direction,
                           @RequestParam(required = false) String breed,
                           @RequestParam(required = false) Integer minAge,
                           HttpSession session) {
        Cat cat = catService.getCatById(id);
        if (cat != null && stars >= 1 && stars <= 10) {
            String username = (String) session.getAttribute(AuthController.SESSION_USERNAME);
            ratingService.rateCat(cat, stars, username == null ? "Anonymous" : username);
        }

        StringBuilder redirect = new StringBuilder("redirect:/cats?page=").append(page)
                .append("&size=").append(size)
                .append("&sort=").append(sort)
                .append("&direction=").append(direction);
        if (breed != null && !breed.isBlank()) {
            redirect.append("&breed=").append(breed);
        }
        if (minAge != null) {
            redirect.append("&minAge=").append(minAge);
        }
        return redirect.toString();
    }
}
