package team.anonyms.athletelink.authorisation.controllers;

import jakarta.servlet.http.HttpServletRequest;
import team.anonyms.athletelink.authorisation.dto.controller.authorisation.CredentialsControllerDto;
import team.anonyms.athletelink.authorisation.dto.controller.authorisation.LoginResultControllerDto;
import team.anonyms.athletelink.authorisation.mappers.AuthorisationMapper;
import team.anonyms.athletelink.authorisation.services.AuthorisationService;
import team.anonyms.athletelink.authorisation.dto.controller.authorisation.RegisterRequestControllerDto;
import team.anonyms.athletelink.authorisation.dto.controller.authorisation.TokenRefreshResponseDto;
import team.anonyms.athletelink.authorisation.dto.controller.authorisation.SendCodeRequestControllerDto;
import team.anonyms.athletelink.authorisation.dto.controller.authorisation.VerifyCodeRequestControllerDto;
import team.anonyms.athletelink.authorisation.dto.controller.authorisation.ResetPasswordRequestControllerDto;
import team.anonyms.athletelink.authorisation.services.VerificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthorisationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthorisationController.class);

    private final AuthorisationService authorisationService;
    private final AuthorisationMapper authorisationMapper;

    private final VerificationService verificationService;

    public AuthorisationController(
            AuthorisationService authorisationService,
            AuthorisationMapper authorisationMapper,
            VerificationService verificationService
    ) {
        this.authorisationService = authorisationService;
        this.authorisationMapper = authorisationMapper;
        this.verificationService = verificationService;
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

        var serviceDto = authorisationMapper.credentialsControllerToServiceDto(
                credentialsControllerDto,
                request.getHeader("User-Agent"),
                request.getRemoteAddr()
        );

        var loginResult = authorisationService.login(serviceDto);

        if (!loginResult.success()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", loginResult.refreshToken())
                .httpOnly(true)
                .secure(false) // TODO: true in prod (when we will implement HTTPS)
                .path("/")
                .maxAge(30 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(authorisationMapper.loginResultServiceToControllerDto(loginResult));
    }

    @PostMapping("/register")
    public ResponseEntity<UUID> register(
            @RequestBody @Valid RegisterRequestControllerDto dto 
    ) {
        logger.info("Called register; email={}", dto.email());

        try {
            var serviceDto = authorisationMapper.registerControllerToServiceDto(dto);
            
            UUID newUserId = authorisationService.register(serviceDto);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(newUserId);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDto> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        logger.info("Called refresh token endpoint");

        if (refreshToken == null) {
            logger.warn("Refresh token cookie is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String newAccessToken = authorisationService.updateAccessToken(refreshToken);
            
            return ResponseEntity.ok(new TokenRefreshResponseDto(newAccessToken));
            
        } catch (IllegalArgumentException e) {
            logger.warn("Refresh failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/send-code")
    public ResponseEntity<Void> sendVerificationCode(
            @RequestBody @Valid SendCodeRequestControllerDto dto
    ) {
        logger.info("Request to send verification code for email: {}", dto.email());
        try {
            verificationService.generateAndSendEmailCode(dto.email());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to send code: {}", e.getMessage());
            // Возвращаем 400, если юзер не найден или уже верифицирован
            return ResponseEntity.badRequest().build(); 
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyEmail(
            @RequestBody @Valid VerifyCodeRequestControllerDto dto
    ) {
        logger.info("Request to verify email: {}", dto.email());
        try {
            verificationService.verifyEmail(dto.email(), dto.code());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Email verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        logger.info("Called logout");

        if (refreshToken != null) {
            authorisationService.logout(refreshToken);
        }

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // TODO: true in prod
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @RequestBody @Valid SendCodeRequestControllerDto dto
    ) {
        logger.info("Request to send password reset code for email: {}", dto.email());
        try {
            verificationService.generateAndSendPasswordResetCode(dto.email());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to send password reset code: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @RequestBody @Valid ResetPasswordRequestControllerDto dto
    ) {
        logger.info("Request to reset password for email: {}", dto.email());
        try {
            verificationService.validateAndConsumePasswordResetCode(dto.email(), dto.code());
            
            authorisationService.resetPassword(dto.email(), dto.newPassword());
            
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Password reset failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
