package com.cpan228.catcritic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "RATINGS")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cat_id")
    private Cat cat;

    @NotNull(message = "Stars is required")
    @Min(value = 1, message = "Rating must be at least 1 star")
    @Max(value = 10, message = "Rating must be at most 10 stars")
    private Integer stars;

    @Column(name = "rater_username")
    private String raterUsername;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
