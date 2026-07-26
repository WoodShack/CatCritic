package com.cpan228.catcritic.repository;

import com.cpan228.catcritic.model.Breed;
import com.cpan228.catcritic.model.Cat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatRepository extends JpaRepository<Cat, Long> {

    Page<Cat> findByBreed(Breed breed, Pageable pageable);

    Page<Cat> findByAgeGreaterThanEqual(int minAge, Pageable pageable);

    Page<Cat> findByBreedAndAgeGreaterThanEqual(Breed breed, int minAge, Pageable pageable);
}
