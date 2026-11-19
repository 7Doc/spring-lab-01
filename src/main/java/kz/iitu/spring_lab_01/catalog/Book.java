package kz.iitu.spring_lab_01.catalog;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record Book(
        Long id,
        @NotBlank(message = "title must not be blank") String title,
        @NotBlank(message = "author must not be blank") String author,
        @Min(value = 1, message = "year must be positive") int year
) {}