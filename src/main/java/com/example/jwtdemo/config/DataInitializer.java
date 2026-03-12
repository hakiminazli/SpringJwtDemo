package com.example.jwtdemo.config;

import com.example.jwtdemo.entity.AppUser;
import com.example.jwtdemo.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initUser(AppUserRepository appUserRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            if (appUserRepository.findByUsername("hakimin").isEmpty()) {
                AppUser user = new AppUser();
                user.setUsername("hakimin");
                user.setPassword(passwordEncoder.encode("password123"));
                user.setRole("USER");

                appUserRepository.save(user);
            }
        };
    }
}