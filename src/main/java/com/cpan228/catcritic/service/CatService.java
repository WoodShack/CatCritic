package com.cpan228.catcritic.service;

import com.cpan228.catcritic.model.Breed;
import com.cpan228.catcritic.model.Cat;
import com.cpan228.catcritic.repository.CatRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CatService {

    private static final Path UPLOAD_DIR = Path.of("uploads");

    private final CatRepository catRepository;

    public CatService(CatRepository catRepository) {
        this.catRepository = catRepository;
    }

    public Cat getCatById(Long id) {
        return catRepository.findById(id).orElse(null);
    }

    public Cat saveCat(Cat cat, MultipartFile photo, String ownerUsername) throws IOException {
        cat.setImageUrl(storePhoto(photo));
        cat.setOwnerUsername(ownerUsername);
        cat.setCreatedAt(LocalDateTime.now());
        cat.setRatingCount(0);
        cat.setRatingTotal(0);
        return catRepository.save(cat);
    }

    public Cat updateCat(Long id, Cat updates, MultipartFile newPhoto) throws IOException {
        Cat existing = getCatById(id);
        if (existing == null) {
            return null;
        }
        existing.setName(updates.getName());
        existing.setAge(updates.getAge());
        existing.setBreed(updates.getBreed());
        existing.setDescription(updates.getDescription());
        if (newPhoto != null && !newPhoto.isEmpty()) {
            existing.setImageUrl(storePhoto(newPhoto));
        }
        return catRepository.save(existing);
    }

    public void deleteCat(Long id) {
        catRepository.deleteById(id);
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
