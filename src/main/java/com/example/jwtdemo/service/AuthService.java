package com.example.jwtdemo.service;

import com.example.jwtdemo.dto.request.LoginRequest;
import com.example.jwtdemo.dto.response.AuthResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

	AuthResponse login(LoginRequest request);

	AuthResponse refreshToken(String refreshToken);

	void logout(String refreshToken);

}
