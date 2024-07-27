package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ReleaseDateConstraintValidator implements ConstraintValidator<FilmReleaseDate, LocalDate> {

    @Override
    public void initialize(FilmReleaseDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) {
            return false;
        }
        return !localDate.isBefore(LocalDate.of(1890, 3, 26)) && !localDate.isAfter(LocalDate.now());
    }
}

