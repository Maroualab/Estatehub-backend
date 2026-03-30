package com.estatehub.backend.services;

import com.estatehub.backend.models.RefreshToken;

public interface IRefreshTokenService {
    RefreshToken createRefreshToken(Long userId);
    RefreshToken verifyExpiration(RefreshToken token);
}
