package team.anonyms.athletelink.authservice.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import team.anonyms.athletelink.authservice.entity.JwtRefreshToken;
import team.anonyms.athletelink.authservice.entity.UserCredentials;
import team.anonyms.athletelink.authservice.repository.JwtRefreshTokenRepository;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;

/**
 * {@code JwtService} is dedicated to handle most of JWT-related things.
 */
@Service
@RequiredArgsConstructor
public class JwtService {
    public static final Duration REFRESH_TOKEN_DURATION = Duration.of(30, ChronoUnit.DAYS);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.of(5, ChronoUnit.MINUTES);

    private final JwtRefreshTokenRepository jwtRefreshTokenRepository;

    private SecretKey key;

    @Value("${jwt.secret}")
    private String secret;

    @PostConstruct
    private void init() {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);

        if (secretBytes.length < 64) {
            throw new IllegalArgumentException("JWT_SECRET must be at least 64 UTF-8 bytes for HS512");
        }

        this.key = Keys.hmacShaKeyFor(secretBytes);
    }

    /**
     * Generates a new JWT token based on user ID.
     *
     * @param userId user ID.
     * @param expiration expiration of the new JWT token.
     *
     * @return JWT token.
     */
    private String generateJwtToken(
            UUID userId,
            Instant expiration,
            UserCredentials.UserRoleType role,
            String jwtTokenType
    ) {
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId.toString())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(expiration))
                .claim("role", role)
                .claim("type", jwtTokenType)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    private String generateAccessToken(UserCredentials userCredentials) {
        Instant expiration = Instant.now().plus(ACCESS_TOKEN_DURATION);
        return generateJwtToken(
                userCredentials.getUserId(),
                expiration,
                userCredentials.getRole(),
                "ACCESS"
        );
    }

    private String generateRefreshToken(UserCredentials userCredentials) {
        Instant expiration = Instant.now().plus(REFRESH_TOKEN_DURATION);
        return generateJwtToken(
                userCredentials.getUserId(),
                expiration,
                userCredentials.getRole(),
                "REFRESH"
        );
    }

    public String createOrUpdateRefreshToken(UserCredentials userCredentials, String userAgent, String ipAddress) {
        Instant now = Instant.now();
        Instant expiration = now.plus(REFRESH_TOKEN_DURATION);
        String refreshToken = generateRefreshToken(userCredentials);

        JwtRefreshToken jwtRefreshToken = JwtRefreshToken.builder()
                .jti(UUID.fromString(extractId(refreshToken)))
                .token(hash(refreshToken))
                .expiration(expiration)
                .lastUsed(now)
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .userCredentials(userCredentials)
                .build();

        jwtRefreshTokenRepository.save(jwtRefreshToken);
        return refreshToken;
    }

    private Claims extractClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public String extractId(String token) {
        return extractClaims(token).getId();
    }

    public Instant extractExpiration(String token) {
        return extractClaims(token).getExpiration().toInstant();
    }

    public String extractType(String token) {
        return extractClaims(token).get("type", String.class);
    }

    /**
     * @param jwtToken any provided {@link String} instance.
     *
     * @return {@code true} if provided {@code jwtToken} is valid, {@code false} if {@code jwtToken} is expired,
     * or it is a string with whitespaces only, empty string, or {@code jwtToken} is null.
     */
    public Boolean isValid(@Nullable String jwtToken) {
        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            return false;
        }

        try {
            if (extractExpiration(jwtToken).isBefore(Instant.now())) {
                return false;
            }

            extractClaims(jwtToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Checks provided JWT refresh token and generates a new JWT access token.
     *
     * @param refreshToken token from cookie.
     *
     * @return new access token.
     *
     * @throws IllegalArgumentException if {@code refreshToken} is invalid or outdated.
     * @throws EntityNotFoundException if {@code refreshToken} is not found.
     */
    @Transactional
    public String refreshAccessToken(String refreshToken) {
        if (!isValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid JWT signature");
        }

        if (!"REFRESH".equals(extractType(refreshToken))) {
            throw new IllegalArgumentException("Not refresh token was provided for refreshing");
        }

        JwtRefreshToken dbToken = jwtRefreshTokenRepository.findByToken(hash(refreshToken)).orElseThrow(
                () -> new EntityNotFoundException("Refresh token not found")
        );

        if (dbToken.getExpiration().isBefore(Instant.now())) {
            jwtRefreshTokenRepository.delete(dbToken);
            throw new IllegalArgumentException("Refresh token is expired");
        }

        dbToken.setLastUsed(Instant.now());
        jwtRefreshTokenRepository.save(dbToken);

        return generateAccessToken(dbToken.getUserCredentials());
    }

    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        jwtRefreshTokenRepository.findById(UUID.fromString(extractId(refreshToken))).ifPresent(jwtRefreshTokenRepository::delete);
    }

    public String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-512").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch(NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-512 is not available", e);
        }
    }
}
