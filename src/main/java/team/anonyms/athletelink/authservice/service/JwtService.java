package team.anonyms.athletelink.authservice.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import team.anonyms.athletelink.authservice.entity.JwtRefreshToken;
import team.anonyms.athletelink.authservice.entity.User;
import team.anonyms.athletelink.authservice.repository.JwtRefreshTokenRepository;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
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

    // Secret key for validating or creating JWT tokens
    private SecretKey key;

    @Value("${jwt.secret}")
    private String secret; // TODO: add JWT secret to application.yaml

    @PostConstruct
    private void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    /**
     * Generates a new JWT token based on user ID.
     *
     * @param userId user ID.
     * @param expiration expiration of the new JWT token.
     *
     * @return JWT token.
     */
    private String generateJwtToken(UUID userId, Instant expiration, String jwtTokenType) {
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId.toString())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(expiration))
                .claim("type", jwtTokenType)
                .signWith(key)
                .compact();
    }

    private String generateAccessToken(User user) {
        Instant expiration = Instant.now().plus(ACCESS_TOKEN_DURATION);
        return generateJwtToken(user.getId(), expiration, "ACCESS");
    }

    private String generateRefreshToken(User user) {
        Instant expiration = Instant.now().plus(REFRESH_TOKEN_DURATION);
        return generateJwtToken(user.getId(), expiration, "REFRESH");
    }

    /**
     * Looks for existed JWT refresh tokens in database and checks if any of tokens matches to the current device.
     * If JWT refresh token has already been existed this method updated the expiry date, if it hasn't been existed
     * this method add new one to database.
     */
    @Transactional
    public String createOrUpdateRefreshToken(User user, String userAgent, String ipAddress) {
        List<JwtRefreshToken> jwtRefreshTokens = jwtRefreshTokenRepository.findAllByUserId(user.getId());

        for (JwtRefreshToken jwtRefreshToken : jwtRefreshTokens) {
            if (jwtRefreshToken.getUserAgent().equals(userAgent)) {
                jwtRefreshToken.setExpiration(Instant.now().plus(REFRESH_TOKEN_DURATION));
                jwtRefreshToken.setLastUsed(Instant.now());
                jwtRefreshTokenRepository.save(jwtRefreshToken);
                return jwtRefreshToken.getToken();
            }
        }

        Instant now = Instant.now();
        Instant expiration = now.plus(REFRESH_TOKEN_DURATION);
        String refreshToken = generateRefreshToken(user);

        JwtRefreshToken jwtRefreshToken = JwtRefreshToken.builder()
                .jti(UUID.fromString(extractId(refreshToken)))
                .token(refreshToken)
                .expiration(expiration)
                .lastUsed(now)
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .user(user)
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

    /**
     * Extracts user ID from a JWT token.
     *
     * @param token JWT token
     *
     * @return user ID
     */
    public String extractUserId(String token) {
        return extractClaims(token).getSubject();
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

        JwtRefreshToken dbToken = jwtRefreshTokenRepository.findByToken(refreshToken).orElseThrow(
                () -> new EntityNotFoundException("Refresh token not found")
        );

        if (dbToken.getExpiration().isBefore(Instant.now())) {
            jwtRefreshTokenRepository.delete(dbToken);
            throw new IllegalArgumentException("Refresh token is expired");
        }

        dbToken.setLastUsed(Instant.now());
        jwtRefreshTokenRepository.save(dbToken);

        return generateAccessToken(dbToken.getUser());
    }

    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        jwtRefreshTokenRepository.findById(UUID.fromString(extractId(refreshToken))).ifPresent(jwtRefreshTokenRepository::delete);
    }
}
