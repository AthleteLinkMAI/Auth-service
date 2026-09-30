package team.anonyms.athletelink.authservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import team.anonyms.athletelink.authservice.dto.controller.auth.CredentialsControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.auth.LoginResultControllerDto;
import team.anonyms.athletelink.authservice.dto.service.auth.LoginResultServiceDto;
import team.anonyms.athletelink.authservice.mapper.AuthorisationMapper;
import team.anonyms.athletelink.authservice.service.AuthenticationService;
import team.anonyms.athletelink.authservice.service.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final AuthorisationMapper authorisationMapper;

    /**
     * Creates new cookie with {@code jwtToken}.
     *
     * @param jwtToken JWT token.
     */
    public static ResponseCookie createJwtRefreshTokenCookie(String jwtToken) {
        return ResponseCookie.from("jwtToken", jwtToken)
                .path("/")
                .maxAge(JwtService.REFRESH_TOKEN_DURATION)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResultControllerDto> login(
            @RequestBody CredentialsControllerDto credentialsControllerDto,
            HttpServletRequest request
    ) {
        log.info("Called login; email={}", credentialsControllerDto.email());

        if (credentialsControllerDto.email() == null || credentialsControllerDto.password() == null) {
            return ResponseEntity.badRequest().build();
        }

        LoginResultServiceDto loginResultServiceDto = authenticationService.login(
                authorisationMapper.credentialsControllerToServiceDto(
                        credentialsControllerDto,
                        request.getHeader("User-Agent"),
                        request.getRemoteAddr()
                )
        );

        if (!loginResultServiceDto.success()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createJwtRefreshTokenCookie(loginResultServiceDto.refreshToken()).toString())
                .body(authorisationMapper.loginResultServiceToControllerDto(loginResultServiceDto));
    }

    @PostMapping("/refresh")
    // TODO: add refreshToken rotation
    public ResponseEntity<String> refresh(@CookieValue(name = "refreshToken") String refreshToken) {
        log.info("Called refresh");

        return ResponseEntity.ok(jwtService.refreshAccessToken(refreshToken));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(
            @NonNull HttpServletRequest request,
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        log.info("Called logout");

        if (refreshToken != null) {
            authenticationService.logout(refreshToken, request.getHeader("Authorization").substring(7));
        }

        ResponseCookie cookieDeleted = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // true in prod
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, cookieDeleted.toString()).build();
    }
}
