package com.example.jwtdemo.service.impl;

import com.example.jwtdemo.dto.request.LoginRequest;
import com.example.jwtdemo.dto.request.RegisterRequest;
import com.example.jwtdemo.dto.response.AuthResponse;
import com.example.jwtdemo.entity.AppUser;
import com.example.jwtdemo.entity.RefreshToken;
import com.example.jwtdemo.repository.AppUserRepository;
import com.example.jwtdemo.security.JwtService;
import com.example.jwtdemo.service.AuthService;
import com.example.jwtdemo.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthServiceImpl implements AuthService {

	private static final String TOKEN_TYPE_BEARER = "Bearer";

	private final AuthenticationManager authenticationManager;

	private final AppUserRepository appUserRepository;

	private final JwtService jwtService;

	private final RefreshTokenService refreshTokenService;

	private final PasswordEncoder passwordEncoder;

	private final String defaultRole;

	public AuthServiceImpl(AuthenticationManager authenticationManager, AppUserRepository appUserRepository,
			JwtService jwtService, RefreshTokenService refreshTokenService, PasswordEncoder passwordEncoder,
			@Value("${app.security.default-role:USER}") String defaultRole) {
		this.authenticationManager = authenticationManager;
		this.appUserRepository = appUserRepository;
		this.jwtService = jwtService;
		this.refreshTokenService = refreshTokenService;
		this.passwordEncoder = passwordEncoder;
		this.defaultRole = defaultRole;
	}

	@Override
	public AuthResponse login(LoginRequest request) {
		authenticationManager
			.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		AppUser appUser = appUserRepository.findByUsername(request.getUsername())
			.orElseThrow(() -> new RuntimeException("User not found"));

		return buildAuthResponse(appUser);
	}

	@Override
	public AuthResponse register(RegisterRequest request) {
		if (appUserRepository.findByUsername(request.getUsername()).isPresent()) {
			throw new IllegalArgumentException("Username already exists");
		}

		AppUser appUser = AppUser.builder()
			.username(request.getUsername())
			.password(passwordEncoder.encode(request.getPassword()))
			.role(defaultRole)
			.build();

		AppUser savedUser = appUserRepository.save(appUser);
		return buildAuthResponse(savedUser);
	}

	@Override
	public AuthResponse refreshToken(String refreshToken) {
		RefreshToken savedRefreshToken = refreshTokenService.verifyRefreshToken(refreshToken);
		AppUser appUser = savedRefreshToken.getUser();

		UserDetails userDetails = buildUserDetails(appUser);

		String newAccessToken = jwtService.generateAccessToken(userDetails);
		return new AuthResponse(newAccessToken, refreshToken, TOKEN_TYPE_BEARER);
	}

	@Override
	public void logout(String refreshToken) {
		refreshTokenService.revokeRefreshToken(refreshToken);
	}

	private AuthResponse buildAuthResponse(AppUser appUser) {
		UserDetails userDetails = buildUserDetails(appUser);
		String accessToken = jwtService.generateAccessToken(userDetails);
		String refreshToken = jwtService.generateRefreshToken(userDetails);

		refreshTokenService.createRefreshToken(appUser, refreshToken);

		return new AuthResponse(accessToken, refreshToken, TOKEN_TYPE_BEARER);
	}

	private UserDetails buildUserDetails(AppUser appUser) {
		return User.builder()
			.username(appUser.getUsername())
			.password(appUser.getPassword())
			.authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + appUser.getRole())))
			.build();
	}

}
