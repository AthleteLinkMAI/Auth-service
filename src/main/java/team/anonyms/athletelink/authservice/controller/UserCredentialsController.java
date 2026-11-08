package team.anonyms.athletelink.authservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import team.anonyms.athletelink.authservice.dto.controller.user.*;
import team.anonyms.athletelink.authservice.entity.UserCredentials;
import team.anonyms.athletelink.authservice.mapper.UserCredentialsMapper;
import team.anonyms.athletelink.authservice.service.UserCredentialsService;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
@RequestMapping("/user")
public class UserCredentialsController {
    private final UserCredentialsService userCredentialsService;
    private final UserCredentialsMapper userCredentialsMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("permitAll()")
    @PostMapping
    public void register(
            @RequestBody @Valid RegisterRequestControllerDto registerRequestControllerDto
    ) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "POST /user/register")
                .log();

        userCredentialsService.register(userCredentialsMapper.registerControllerToServiceDto(registerRequestControllerDto));
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/password")
    public void changePassword(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody @Valid ChangePasswordRequestControllerDto changePasswordRequestControllerDto
    ) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "PUT /user/change-password/{userId}")
                .addKeyValue("userId", userId)
                .log();

        userCredentialsService.changePassword(
                userCredentialsMapper.changePasswordRequestControllerToServiceDto(userId, changePasswordRequestControllerDto)
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/email")
    public void changeEmail(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody @Email String newEmail
    ) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "PUT /user/change-email/{userId}")
                .addKeyValue("userId", userId)
                .log();

        userCredentialsService.changeEmail(userId, newEmail);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/role")
    public UserCredentialsControllerDto updateUserRole(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") UserCredentials.UserRoleType role,
            @RequestBody UpdateRoleRequestControllerDto dto
    ) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "PUT /user/role")
                .addKeyValue("userId", userId)
                .addKeyValue("role", role)
                .addKeyValue("updateRoleRequestControllerDto", dto)
                .log();

        return userCredentialsMapper.userServiceToControllerDto(
                userCredentialsService.updateUserRole(userCredentialsMapper.updateRoleRequestControllerToServiceDto(dto))
        );
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void deleteUser(@RequestHeader("X-User-Id") UUID userId) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "DELETE /user/delete/{userId}")
                .addKeyValue("userId", userId)
                .log();

        userCredentialsService.deleteUser(userId);
    }
}
