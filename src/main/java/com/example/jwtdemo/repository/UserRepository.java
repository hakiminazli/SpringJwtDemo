package com.example.jwtdemo.repository;

import com.example.jwtdemo.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {

	boolean existByUsername(String username);

}
