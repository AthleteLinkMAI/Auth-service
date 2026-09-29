package team.anonyms.athletelink.authservice.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import team.anonyms.athletelink.authservice.entity.User;
import team.anonyms.athletelink.authservice.repository.JwtRefreshTokenRepository;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class JwtServiceTest {

    @Test
    void signsAccessTokensWithUtf8SecretAndHs512() {
        String secret = "integration-test-secret-".repeat(4);
        JwtService service = new JwtService(mock(JwtRefreshTokenRepository.class));
        ReflectionTestUtils.setField(service, "secret", secret);
        ReflectionTestUtils.invokeMethod(service, "init");

        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).build();
        String accessToken = ReflectionTestUtils.invokeMethod(service, "generateAccessToken", user);

        var claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(accessToken);

        assertEquals("HS512", claims.getHeader().getAlgorithm());
        assertEquals(userId.toString(), claims.getPayload().getSubject());
        assertEquals("ACCESS", claims.getPayload().get("type", String.class));
    }

    @Test
    void rejectsSecretShorterThanHs512Requires() {
        JwtService service = new JwtService(mock(JwtRefreshTokenRepository.class));
        ReflectionTestUtils.setField(service, "secret", "too-short");

        assertThrows(IllegalArgumentException.class,
                () -> ReflectionTestUtils.invokeMethod(service, "init"));
    }
}
