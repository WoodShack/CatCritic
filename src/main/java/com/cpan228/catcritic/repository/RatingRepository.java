package com.cpan228.catcritic.repository;

import com.cpan228.catcritic.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
