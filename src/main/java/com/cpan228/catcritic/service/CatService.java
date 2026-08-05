package com.cpan228.catcritic.service;

import com.cpan228.catcritic.model.Breed;
import com.cpan228.catcritic.model.Cat;
import com.cpan228.catcritic.repository.CatRepository;
import com.cpan228.catcritic.repository.RatingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CatService {

    private static final Path UPLOAD_DIR = Path.of("uploads");

    private final CatRepository catRepository;
    private final RatingRepository ratingRepository;

    public CatService(CatRepository catRepository, RatingRepository ratingRepository) {
        this.catRepository = catRepository;
        this.ratingRepository = ratingRepository;
    }

    public Cat getCatById(Long id) {
        return catRepository.findById(id).orElse(null);
    }

    public List<Cat> findAllCats() {
        return catRepository.findAll();
    }

    @Transactional
    public void deleteCat(Long id) {
        ratingRepository.deleteByCatId(id);
        catRepository.deleteById(id);
    }

    public Cat saveCat(Cat cat, MultipartFile photo, String ownerUsername) throws IOException {
        cat.setImageUrl(storePhoto(photo));
        cat.setOwnerUsername(ownerUsername);
        cat.setCreatedAt(LocalDateTime.now());
        cat.setRatingCount(0);
        cat.setRatingTotal(0);
        return catRepository.save(cat);
    }

    public Page<Cat> browse(Breed breed, Integer minAge, Pageable pageable) {
        if (breed != null && minAge != null) {
            return catRepository.findByBreedAndAgeGreaterThanEqual(breed, minAge, pageable);
        } else if (breed != null) {
            return catRepository.findByBreed(breed, pageable);
        } else if (minAge != null) {
            return catRepository.findByAgeGreaterThanEqual(minAge, pageable);
        }
        return catRepository.findAll(pageable);
    }

    private String storePhoto(MultipartFile photo) throws IOException {
        Files.createDirectories(UPLOAD_DIR);
        String extension = "";
        String originalName = photo.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }
        String storedName = UUID.randomUUID() + extension;
        Path target = UPLOAD_DIR.resolve(storedName);
        photo.transferTo(target);
        return "/uploads/" + storedName;
    }
}
