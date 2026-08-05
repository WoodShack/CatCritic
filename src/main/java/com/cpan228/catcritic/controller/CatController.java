package com.cpan228.catcritic.controller;

import com.cpan228.catcritic.model.Breed;
import com.cpan228.catcritic.model.Cat;
import com.cpan228.catcritic.model.Rating;
import com.cpan228.catcritic.model.User;
import com.cpan228.catcritic.service.CatService;
import com.cpan228.catcritic.service.RatingService;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private boolean canEdit(User user, Cat cat) {
        if (user == null || cat == null) {
            return false;
        }

        return user.isAdmin()
                || (user.isCatOwner()
                && user.getUsername().equalsIgnoreCase(cat.getOwnerUsername()));
    }

    @GetMapping("/cats/new")
    public String showSubmitForm(Model model) {
        model.addAttribute("cat", new Cat());
        model.addAttribute("breeds", Breed.values());
        return "cat-form";
    }

    @PostMapping("/cats/new")
    public String submitCat(@Valid @ModelAttribute("cat") Cat cat,
                            BindingResult result,
                            @RequestParam("photo") MultipartFile photo,
                            Model model,
                            @AuthenticationPrincipal User currentUser) {

        if (photo.isEmpty()) {
            result.reject("photo.required", "A photo of your cat is required");
        } else if (photo.getContentType() == null ||
                !photo.getContentType().startsWith("image/")) {
            result.reject("photo.invalid", "The uploaded file must be an image");
        }

        if (result.hasErrors()) {
            model.addAttribute("breeds", Breed.values());
            return "cat-form";
        }

        try {
            Cat saved = catService.saveCat(
                    cat,
                    photo,
                    currentUser.getUsername()
            );

            return "redirect:/cats/" + saved.getId();

        } catch (IOException e) {
            model.addAttribute("breeds", Breed.values());
            model.addAttribute(
                    "uploadError",
                    "We couldn't save that photo. Please try again."
            );
            return "cat-form";
        }
    }


    @GetMapping("/cats/{id}")
    public String showCat(@PathVariable Long id,
                          Model model,
                          @AuthenticationPrincipal User currentUser) {

        Cat cat = catService.getCatById(id);

        if (cat == null) {
            return "redirect:/cats";
        }

        boolean loggedIn = currentUser != null;
        Rating userRating = null;

        if (loggedIn) {
            userRating = ratingService.getUserRatingForCat(
                    cat.getId(),
                    currentUser.getUsername()
            );
        }

        model.addAttribute("cat", cat);
        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("userRating", userRating);
        model.addAttribute("canEdit", canEdit(currentUser, cat));
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
            Model model,
            @AuthenticationPrincipal User currentUser) {


        String sortField = SORTABLE_FIELDS.contains(sort)
                ? sort
                : "createdAt";

        Sort.Direction sortDirection;

        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException e) {
            sortDirection = Sort.Direction.DESC;
        }


        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(sortDirection, sortField)
                );


        Breed breedFilter = null;

        if (breed != null && !breed.isBlank()) {
            try {
                breedFilter = Breed.valueOf(breed);
            } catch (IllegalArgumentException ignored) {
            }
        }


        Page<Cat> catPage =
                catService.browse(
                        breedFilter,
                        minAge,
                        pageable
                );


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


        boolean loggedIn = currentUser != null;

        model.addAttribute("loggedIn", loggedIn);


        if (loggedIn) {

            Map<Long, Integer> myRatings = new HashMap<>();

            for (Cat cat : catPage.getContent()) {

                Rating rating =
                        ratingService.getUserRatingForCat(
                                cat.getId(),
                                currentUser.getUsername()
                        );

                if (rating != null) {
                    myRatings.put(
                            cat.getId(),
                            rating.getStars()
                    );
                }
            }

            model.addAttribute("myRatings", myRatings);
        }


        return "browse";
    }


    @PostMapping("/cats/{id}/rate")
    public String rateCat(
            @PathVariable Long id,
            @RequestParam int stars,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) Integer minAge,
            @AuthenticationPrincipal User currentUser) {


        Cat cat = catService.getCatById(id);

        if (cat != null &&
                stars >= 1 &&
                stars <= 10 &&
                currentUser != null) {

            ratingService.rateCat(
                    cat,
                    stars,
                    currentUser.getUsername()
            );
        }


        return "redirect:/cats?page=" + page
                + "&size=" + size
                + "&sort=" + sort
                + "&direction=" + direction;
    }


    @GetMapping("/cats/{id}/edit")
    public String showEditForm(
            @PathVariable Long id,
            Model model,
            @AuthenticationPrincipal User currentUser) {
        Cat cat = catService.getCatById(id);

        if (cat == null) {
            return "redirect:/cats";
        }

        if (!canEdit(currentUser, cat)) {
            throw new AccessDeniedException(
                    "You are not allowed to edit this cat."
            );
        }
        model.addAttribute("cat", cat);
        model.addAttribute("breeds", Breed.values());
        return "cat-edit";
    }

    @PostMapping("/cats/{id}/edit")
    public String editCat(
            @PathVariable Long id,
            @Valid @ModelAttribute("cat") Cat cat,
            BindingResult result,
            @RequestParam(required = false) MultipartFile photo,
            Model model,
            @AuthenticationPrincipal User currentUser) {

        Cat existing = catService.getCatById(id);


        if (!canEdit(currentUser, existing)) {
            throw new AccessDeniedException(
                    "You are not allowed to edit this cat."
            );
        }

        if (result.hasErrors()) {
            model.addAttribute("breeds", Breed.values());
            return "cat-edit";
        }

        try {
            catService.updateCat(id, cat, photo);
            return "redirect:/cats/" + id;

        } catch (IOException e) {
            return "cat-edit";
        }
    }
    @PostMapping("/cats/{id}/delete")
    public String deleteCat(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {


        Cat cat = catService.getCatById(id);

        if (!canEdit(currentUser, cat)) {
            throw new AccessDeniedException(
                    "You are not allowed to delete this cat."
            );
        }
        catService.deleteCat(id);
        return "redirect:/cats";
    }
}