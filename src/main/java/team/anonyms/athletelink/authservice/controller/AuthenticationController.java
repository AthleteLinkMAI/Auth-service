package team.anonyms.athletelink.authservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import team.anonyms.athletelink.authservice.dto.controller.auth.CredentialsControllerDto;
import team.anonyms.athletelink.authservice.dto.service.auth.LoginResultDto;
import team.anonyms.athletelink.authservice.mapper.AuthorisationMapper;
import team.anonyms.athletelink.authservice.service.AuthenticationService;
import team.anonyms.athletelink.authservice.service.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final AuthorisationMapper authorisationMapper;

    /**
     * Creates new cookie which stores JWT refresh token in itself.
     *
     * @param jwtToken JWT token.
     */
    public static ResponseCookie createJwtRefreshTokenCookie(String jwtToken, Duration duration) {
        return ResponseCookie.from("jwtRefreshToken", jwtToken)
                .path("/")
                .httpOnly(true)
                .secure(false) // false
                .maxAge(duration)
                .sameSite("Strict")
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody @Valid CredentialsControllerDto credentialsControllerDto,
            HttpServletRequest request
    ) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "POST /auth/login")
                .log();

        LoginResultDto loginResultServiceDto = authenticationService.login(
                authorisationMapper.credentialsControllerToServiceDto(
                        credentialsControllerDto,
                        request.getHeader("User-Agent"),
                        request.getRemoteAddr()
                )
        );

        if (!loginResultServiceDto.success()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity
                .noContent()
                .header(
                        HttpHeaders.SET_COOKIE,
                        createJwtRefreshTokenCookie(
                                loginResultServiceDto.refreshToken(),
                                JwtService.REFRESH_TOKEN_DURATION
                        ).toString()
                )
                .build();
    }

    @PostMapping("/refresh")
    // TODO: add refreshToken rotation
    public ResponseEntity<String> refresh(@CookieValue(name = "jwtRefreshToken") String refreshToken) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "POST /auth/refresh")
                .log();

        return ResponseEntity.ok(jwtService.refreshAccessToken(refreshToken));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(
            @NonNull HttpServletRequest request,
            @CookieValue(name = "jwtRefreshToken", required = false) String refreshToken
    ) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "DELETE /auth/logout")
                .log();

        if (refreshToken != null) {
            authenticationService.logout(refreshToken, request.getHeader("Authorization").substring(7));
        }

        return ResponseEntity.noContent()
                .header(
                        HttpHeaders.SET_COOKIE,
                        createJwtRefreshTokenCookie(
                                "",
                                Duration.of(0, ChronoUnit.SECONDS)
                        ).toString()
                )
                .build();
    }
}
