package com.example.jwtdemo.service;

import com.example.jwtdemo.entity.AppUser;
import com.example.jwtdemo.entity.RefreshToken;


public interface RefreshTokenService {
    RefreshToken createRefreshToken(AppUser user, String tokenValue);
    RefreshToken verifyRefreshToken(String tokenValue);
    void revokeRefreshToken(String tokenValue);
}
