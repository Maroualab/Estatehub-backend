package com.estatehub.backend.services;

import com.estatehub.backend.exceptions.ResourceNotFoundException;
import com.estatehub.backend.exceptions.TokenRefreshException;
import com.estatehub.backend.models.RefreshToken;
import com.estatehub.backend.models.User;
import com.estatehub.backend.repositories.RefreshTokenRepository;
import com.estatehub.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService implements IRefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    // 7 jours en millisecondes
    private static final long REFRESH_TOKEN_DURATION_MS = 604800000L;

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
            .orElseGet(() -> RefreshToken.builder().user(user).build());

        // Rotation atomique: on met à jour la ligne existante si présente
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_DURATION_MS));

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Refresh token expiré, veuillez vous reconnecter");
        }
        return token;
    }
}
