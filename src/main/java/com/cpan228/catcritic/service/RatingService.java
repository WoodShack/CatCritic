package com.cpan228.catcritic.service;

import com.cpan228.catcritic.model.Cat;
import com.cpan228.catcritic.model.Rating;
import com.cpan228.catcritic.repository.CatRepository;
import com.cpan228.catcritic.repository.RatingRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final CatRepository catRepository;

    public RatingService(
            RatingRepository ratingRepository,
            CatRepository catRepository) {

        this.ratingRepository = ratingRepository;
        this.catRepository = catRepository;
    }


    public Rating getUserRatingForCat(Long catId, String username) {
        return ratingRepository
                .findByCatIdAndRaterUsername(catId, username)
                .orElse(null);
    }


    @Transactional
    public void rateCat(Cat cat, int stars, String username) {

        Rating existing = ratingRepository
                .findByCatIdAndRaterUsername(cat.getId(), username)
                .orElse(null);


        if (existing != null) {

            int oldStars = existing.getStars();

            existing.setStars(stars);
            existing.setCreatedAt(LocalDateTime.now());

            ratingRepository.save(existing);

            cat.setRatingTotal(
                    cat.getRatingTotal() - oldStars + stars
            );

        } else {

            Rating rating = new Rating();

            rating.setCat(cat);
            rating.setStars(stars);
            rating.setRaterUsername(username);
            rating.setCreatedAt(LocalDateTime.now());

            ratingRepository.save(rating);

            cat.setRatingCount(
                    cat.getRatingCount() + 1
            );

            cat.setRatingTotal(
                    cat.getRatingTotal() + stars
            );
        }

        catRepository.save(cat);
    }
}
