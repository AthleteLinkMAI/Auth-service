package team.anonyms.athletelink.authorisation.controllers;

import jakarta.servlet.http.HttpServletRequest;
import team.anonyms.athletelink.authorisation.dto.controller.authorisation.CredentialsControllerDto;
import team.anonyms.athletelink.authorisation.dto.controller.authorisation.LoginResultControllerDto;
import team.anonyms.athletelink.authorisation.mappers.AuthorisationMapper;
import team.anonyms.athletelink.authorisation.services.AuthorisationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/me")
    public ResponseEntity<Void> updateAccessToken(@CookieValue String refreshToken) {
        return ResponseEntity.ok().build();
    }
}
