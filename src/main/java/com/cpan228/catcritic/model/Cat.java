package com.cpan228.catcritic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CATS")
public class Cat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be at least 0")
    @Max(value = 30, message = "Age must be 30 or less")
    private Integer age;

    @NotNull(message = "Breed is required")
    @Enumerated(EnumType.STRING)
    private Breed breed;

    @NotBlank(message = "Description is required")
    @jakarta.validation.constraints.Size(max = 1000, message = "Description must be 1000 characters or fewer")
    @Column(length = 1000)
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "owner_username")
    private String ownerUsername;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Column(name = "rating_count")
    private int ratingCount;

    @Column(name = "rating_total")
    private long ratingTotal;

    public double getAverageRating() {
        return ratingCount == 0 ? 0.0 : (double) ratingTotal / ratingCount;
    }

    public int getRoundedRating() {
        return (int) Math.round(getAverageRating());
    }
}
