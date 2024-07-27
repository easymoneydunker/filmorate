package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.support.GeneratedKeyHolder;

@SpringBootApplication(scanBasePackages = "ru.yandex.practicum.filmorate")
@AutoConfiguration
public class FilmorateApplication {
    public static void main(String[] args) {
        SpringApplication.run(FilmorateApplication.class, args);
        System.out.println(new GeneratedKeyHolder().getKey());
    }
}
