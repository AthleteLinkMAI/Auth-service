package team.anonyms.athletelink.authservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team.anonyms.athletelink.authservice.dto.controller.user.ChangePasswordRequestControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.user.RegisterRequestControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.user.UserUpdateControllerDto;
import team.anonyms.athletelink.authservice.mapper.UserMapper;
import team.anonyms.athletelink.authservice.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody @Valid RegisterRequestControllerDto registerRequestControllerDto
    ) {
        log.info("Called register; username={}, email={}", registerRequestControllerDto.username(),
                registerRequestControllerDto.email());

        userService.register(userMapper.registerControllerToServiceDto(registerRequestControllerDto));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/update-user")
    public ResponseEntity<Void> updateUser(
            @RequestBody @Valid UserUpdateControllerDto dto,
            @AuthenticationPrincipal UUID userId
    ) {
        log.info("Called update; userId={}", userId);

        userService.updateUser(userMapper.userUpdateControllerToServiceDto(userId, dto));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestBody @Valid ChangePasswordRequestControllerDto changePasswordRequestControllerDto,
            @AuthenticationPrincipal UUID userId
    ) {
        log.info("Called change-password");

        userService.changePassword(
                userMapper.changePasswordRequestControllerToServiceDto(userId, changePasswordRequestControllerDto)
        );

        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-email")
    public ResponseEntity<Void> changeEmail(
            @RequestBody @Email String newEmail,
            @AuthenticationPrincipal UUID userId
    ) {
        log.info("Called change-email; userId={}, newEmail={}", userId, newEmail);

        userService.changeEmail(userId, newEmail);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UUID userId) {
        log.info("Called delete; userId={}", userId);

        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
