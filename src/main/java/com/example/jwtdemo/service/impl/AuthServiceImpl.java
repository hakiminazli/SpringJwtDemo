package com.example.jwtdemo.service.impl;

import com.example.jwtdemo.dto.request.LoginRequest;
import com.example.jwtdemo.dto.response.AuthResponse;
import com.example.jwtdemo.entity.AppUser;
import com.example.jwtdemo.entity.RefreshToken;
import com.example.jwtdemo.repository.AppUserRepository;
import com.example.jwtdemo.security.JwtService;
import com.example.jwtdemo.service.AuthService;
import com.example.jwtdemo.service.RefreshTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authenticationManager;

	private final AppUserRepository appUserRepository;

	private final JwtService jwtService;

	private final RefreshTokenService refreshTokenService;

	public AuthServiceImpl(AuthenticationManager authenticationManager, AppUserRepository appUserRepository,
			JwtService jwtService, RefreshTokenService refreshTokenService) {
		this.authenticationManager = authenticationManager;
		this.appUserRepository = appUserRepository;
		this.jwtService = jwtService;
		this.refreshTokenService = refreshTokenService;
	}

	@Override
	public AuthResponse login(LoginRequest request) {
		authenticationManager
			.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		AppUser appUser = appUserRepository.findByUsername(request.getUsername())
			.orElseThrow(() -> new RuntimeException("User not found"));

		UserDetails userDetails = User.builder()
			.username(appUser.getUsername())
			.password(appUser.getPassword())
			.roles(appUser.getPassword())
			.build();

		String accessToken = jwtService.generateAccessToken(userDetails);
		String refreshToken = jwtService.generateRefreshToken(userDetails);

		refreshTokenService.createRefreshToken(appUser, refreshToken);

		return new AuthResponse(accessToken, refreshToken);
	}

	@Override
	public AuthResponse refreshToken(String refreshToken) {
		RefreshToken savedRefreshToken = refreshTokenService.verifyRefreshToken(refreshToken);
		AppUser appUser = savedRefreshToken.getUser();

		UserDetails userDetails = User.builder()
			.username(appUser.getUsername())
			.password(appUser.getPassword())
			.roles(appUser.getPassword())
			.build();

		String newAccessToken = jwtService.generateAccessToken(userDetails);
		return new AuthResponse(newAccessToken, refreshToken);
	}

	@Override
	public void logout(String refreshToken) {
		refreshTokenService.revokeRefreshToken(refreshToken);
	}

}
