package com.estatehub.backend.services;

import com.estatehub.backend.dtos.AuthRequest;
import com.estatehub.backend.dtos.AuthResponse;
import com.estatehub.backend.dtos.RegisterRequest;
import com.estatehub.backend.dtos.TokenRefreshRequest;
import com.estatehub.backend.dtos.TokenRefreshResponse;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(AuthRequest request);
    TokenRefreshResponse refreshToken(TokenRefreshRequest request);
    void logout();
}
