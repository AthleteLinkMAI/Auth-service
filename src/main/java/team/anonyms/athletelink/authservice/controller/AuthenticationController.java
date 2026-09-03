package team.anonyms.athletelink.authservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import team.anonyms.athletelink.authservice.dto.controller.auth.CredentialsControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.auth.LoginResultControllerDto;
import team.anonyms.athletelink.authservice.dto.service.auth.LoginResultServiceDto;
import team.anonyms.athletelink.authservice.mapper.AuthorisationMapper;
import team.anonyms.athletelink.authservice.service.AuthenticationService;
import team.anonyms.athletelink.authservice.service.JwtService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

// TODO: add IDOR protection
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final AuthorisationMapper authorisationMapper;

    public AuthenticationController(
            AuthenticationService authenticationService,
            JwtService jwtService,
            AuthorisationMapper authorisationMapper
    ) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.authorisationMapper = authorisationMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResultControllerDto> login(
            @RequestBody CredentialsControllerDto credentialsControllerDto,
            HttpServletRequest request
    ) {
        logger.info("Called login; email={}", credentialsControllerDto.email());

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

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", loginResultServiceDto.refreshToken())
                .httpOnly(true)
                .secure(false) // true in prod (when we will implement HTTPS)
                .path("/")
                .maxAge(30 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(authorisationMapper.loginResultServiceToControllerDto(loginResultServiceDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        logger.info("Called refresh");

        return ResponseEntity.ok(jwtService.refreshAccessToken(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        logger.info("Called logout");

        if (refreshToken != null) {
            authenticationService.logout(refreshToken);
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
