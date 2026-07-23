package team.anonyms.athletelink.authorisation.controllers;

import jakarta.servlet.http.HttpServletRequest;
import team.anonyms.athletelink.authorisation.dto.controller.authorisation.CredentialsControllerDto;
import team.anonyms.athletelink.authorisation.dto.controller.authorisation.LoginResultControllerDto;
import team.anonyms.athletelink.authorisation.mappers.AuthorisationMapper;
import team.anonyms.athletelink.authorisation.services.AuthorisationService;
import team.anonyms.athletelink.authorisation.dto.controller.authorisation.RegisterRequestControllerDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthorisationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthorisationController.class);

    private final AuthorisationService authorisationService;
    private final AuthorisationMapper authorisationMapper;

    public AuthorisationController(
            AuthorisationService authorisationService,
            AuthorisationMapper authorisationMapper
    ) {
        this.authorisationService = authorisationService;
        this.authorisationMapper = authorisationMapper;
    }

    @PostMapping
    public ResponseEntity<LoginResultControllerDto> login(
            @RequestBody CredentialsControllerDto credentialsControllerDto,
            HttpServletRequest request
    ) {
        logger.info("Called login; email={}", credentialsControllerDto.email());

        if (credentialsControllerDto.email() == null || credentialsControllerDto.password() == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                authorisationMapper.loginResultServiceToControllerDto(
                        authorisationService.login(
                                authorisationMapper.credentialsControllerToServiceDto(
                                        credentialsControllerDto,
                                        request.getHeader("User-Agent"),
                                        request.getRemoteAddr()
                                )
                        )
                )
        );
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

    @PostMapping("/me")
    public ResponseEntity<Void> updateAccessToken(@CookieValue String refreshToken) {
        return ResponseEntity.ok().build();
    }
}
