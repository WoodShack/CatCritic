package com.cpan228.catcritic.repository;

import com.cpan228.catcritic.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByCatIdAndRaterUsername(Long catId, String raterUsername);
}
