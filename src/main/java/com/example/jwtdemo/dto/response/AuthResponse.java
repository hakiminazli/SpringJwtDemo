package com.example.jwtdemo.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {

	private String accessToken;

	private String refreshToken;

	private String tokenType;

	public AuthResponse() {
		this.tokenType = "Bearer";
	}

	public AuthResponse(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.tokenType = "Bearer";
	}

}
