package team.anonyms.athletelink.authorisation.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import team.anonyms.athletelink.authorisation.dto.controller.authorisation.CredentialsControllerDto;
import team.anonyms.athletelink.authorisation.dto.controller.authorisation.LoginResultControllerDto;
import team.anonyms.athletelink.authorisation.dto.service.authorisation.LoginResultServiceDto;
import team.anonyms.athletelink.authorisation.mappers.AuthorisationMapper;
import team.anonyms.athletelink.authorisation.services.AuthorisationService;
import team.anonyms.athletelink.authorisation.dto.controller.authorisation.RegisterRequestControllerDto;
import team.anonyms.athletelink.authorisation.dto.controller.authorisation.ResetPasswordRequestControllerDto;
import team.anonyms.athletelink.authorisation.services.JwtService;
import team.anonyms.athletelink.authorisation.services.EmailService;
import team.anonyms.athletelink.authorisation.dto.controller.authorisation.ChangePasswordRequestControllerDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.util.UUID;
import java.security.Principal;

// TODO: add IDOR protection
@RestController
@RequestMapping("/auth")
public class AuthorisationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthorisationController.class);

    private final AuthorisationService authorisationService;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AuthorisationMapper authorisationMapper;

    public AuthorisationController(
            AuthorisationService authorisationService,
            JwtService jwtService,
            EmailService emailService,
            AuthorisationMapper authorisationMapper
    ) {
        this.authorisationService = authorisationService;
        this.jwtService = jwtService;
        this.emailService = emailService;
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

        LoginResultServiceDto loginResultServiceDto = authorisationService.login(
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

    @PostMapping("/register")
    public ResponseEntity<UUID> register(
            @RequestBody @Valid RegisterRequestControllerDto registerRequestControllerDto
    ) {
        logger.info("Called register; username={}, email={}", registerRequestControllerDto.username(),
                registerRequestControllerDto.email());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                authorisationService.register(
                        authorisationMapper.registerControllerToServiceDto(registerRequestControllerDto)
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        logger.info("Called refresh");

        return ResponseEntity.ok(jwtService.refreshAccessToken(refreshToken));
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateUser(
            @RequestBody @Valid ChangePasswordRequestControllerDto dto,
            Principal principal
    ) {
        logger.info("Request to change password for user ID: {}", principal.getName());
        authorisationService.changePassword(UUID.fromString(principal.getName()), dto.oldPassword(), dto.newPassword());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-email")
    public ResponseEntity<Void> updatePassword(
            @RequestBody @Valid ChangePasswordRequestControllerDto dto,
            Principal principal
    ) {
        logger.info("Request to change password for user ID: {}", principal.getName());
        authorisationService.changePassword(UUID.fromString(principal.getName()), dto.oldPassword(), dto.newPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-code")
    public ResponseEntity<Void> sendVerificationCode(@RequestBody @Email String email) {
        logger.info("Called send-code; {}", email);
        
        emailService.generateAndSendEmailCode(email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyEmail(@RequestBody String verificationCode, HttpServletRequest request) {
        logger.info("Called verify; verificationCode={}", verificationCode);

        // TODO: add extracting email by jwtToken
        // verificationService.verifyEmail(dto.email(), dto.code());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        logger.info("Called logout");

        if (refreshToken != null) {
            authorisationService.logout(refreshToken);
        }

        ResponseCookie cookieDeleted = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // true in prod
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookieDeleted.toString())
                .build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Email String email) {
        logger.info("Called forgot-password; email={}", email);

        emailService.generateAndSendPasswordResetCode(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @RequestBody @Valid ResetPasswordRequestControllerDto resetPasswordRequestControllerDto
    ) {
        logger.info("Called reset-password; email={}", resetPasswordRequestControllerDto.email());

        emailService.validateAndConsumePasswordResetCode(
                resetPasswordRequestControllerDto.email(),
                resetPasswordRequestControllerDto.code()
        );

        authorisationService.resetPassword(
                resetPasswordRequestControllerDto.email(),
                resetPasswordRequestControllerDto.newPassword()
        );

        return ResponseEntity.ok().build();
    }
}
