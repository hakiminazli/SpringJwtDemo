package com.example.jwtdemo.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {

	private String accessToken;

	private String refreshToken;

	private String tokenType;

}
