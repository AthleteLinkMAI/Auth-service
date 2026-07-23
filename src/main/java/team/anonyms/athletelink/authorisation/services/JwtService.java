package team.anonyms.athletelink.authorisation.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import team.anonyms.athletelink.authorisation.entities.JwtRefreshToken;
import team.anonyms.athletelink.authorisation.entities.User;
import team.anonyms.athletelink.authorisation.repositories.JwtRefreshTokenRepository;

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
public class JwtService {
    public static final Duration REFRESH_TOKEN_DURATION = Duration.of(30, ChronoUnit.DAYS);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.of(15, ChronoUnit.MINUTES);

    private final JwtRefreshTokenRepository jwtRefreshTokenRepository;

    // Secret key for validating or creating JWT tokens
    private SecretKey key;

    @Value("${jwt.secret}")
    private String secret;

    @PostConstruct
    private void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public JwtService(JwtRefreshTokenRepository jwtRefreshTokenRepository) {
        this.jwtRefreshTokenRepository = jwtRefreshTokenRepository;
    }

    /**
     * <p>
     *     Looks for existed JWT refresh tokens in database and checks if any of tokens matches to the current device.
     *     If JWT refresh token has already been existed this method updated the expiry date, if it hasn't been existed
     *     this method add new one to database.
     * </p>
     */
    @Transactional
    public String createOrUpdateRefreshToken(User user, String userAgent, String ipAddress) {
        List<JwtRefreshToken> existedJwtRefreshTokens = jwtRefreshTokenRepository.findAllByUserId(user.getId());

        for (JwtRefreshToken tokenEntity : existedJwtRefreshTokens) {
            if (tokenEntity.getUserAgent().equals(userAgent)) {
                tokenEntity.setExpiration(Instant.now().plus(REFRESH_TOKEN_DURATION));
                tokenEntity.setLastUsed(Instant.now());
                jwtRefreshTokenRepository.save(tokenEntity);
                return tokenEntity.getToken();
            }
        }

        Instant now = Instant.now();
        Instant expiration = now.plus(REFRESH_TOKEN_DURATION);
        String rawToken = generate(user.getId(), expiration);

        JwtRefreshToken newTokenEntity = JwtRefreshToken.builder()
                .token(rawToken)
                .expiration(expiration)
                .lastUsed(now)
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .user(user)
                .build();

        jwtRefreshTokenRepository.save(newTokenEntity);
        return rawToken;
    }

    public String generateAccessToken(User user) {
        Instant expiration = Instant.now().plus(ACCESS_TOKEN_DURATION);
        return generate(user.getId(), expiration);
    }

    /**
     * Extracts user ID from JWT token.
     *
     * @param token JWT token
     *
     * @return user ID
     */
    public String extractUserId(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    /**
     * @param jwtToken any provided {@link String} instance.
     *
     * @return {@code true} if provided {@code jwtToken} is valid, {@code false} if {@code jwtToken} is not valid,
     * or it is a string with whitespaces only, empty string, or {@code jwtToken} is null.
     */
    public Boolean isValid(@Nullable String jwtToken) {
        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            return false;
        }

        try {
            extractUserId(jwtToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Generates a new JWT token based on user ID.
     *
     * @param userId user ID.
     * @param expiration expiration of the new JWT token.
     *
     * @return JWT token.
     */
    private String generate(UUID userId, Instant expiration) {
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(expiration))
                .signWith(key)
                .compact();
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
