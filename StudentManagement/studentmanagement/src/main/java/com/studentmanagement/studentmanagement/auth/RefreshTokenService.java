package com.studentmanagement.studentmanagement.auth;

import com.studentmanagement.studentmanagement.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user) {

        // Remove old refresh tokens for this user
        refreshTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();

        Instant expiryDate = Instant.now()
                .plusMillis(refreshTokenExpiration);

        RefreshToken refreshToken = new RefreshToken(
                token,
                expiryDate,
                user
        );

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validateRefreshToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token has expired");
        }

        return refreshToken;
    }

    public void revokeRefreshToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid refresh token"));

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }
}