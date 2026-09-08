package team.anonyms.athletelink.authservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(
            UserService userService,
            UserMapper userMapper
    ) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody @Valid RegisterRequestControllerDto registerRequestControllerDto
    ) {
        logger.info("Called register; username={}, email={}", registerRequestControllerDto.username(),
                registerRequestControllerDto.email());

        userService.register(userMapper.registerControllerToServiceDto(registerRequestControllerDto));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/update-user")
    public ResponseEntity<Void> updateUser(
            @RequestBody @Valid UserUpdateControllerDto dto,
            @AuthenticationPrincipal UUID userId
    ) {
        logger.info("Called update; userId={}", userId);

        userService.updateUser(userMapper.userUpdateControllerToServiceDto(userId, dto));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestBody @Valid ChangePasswordRequestControllerDto changePasswordRequestControllerDto,
            @AuthenticationPrincipal UUID userId
    ) {
        logger.info("Called change-password");

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
        logger.info("Called change-email; userId={}, newEmail={}", userId, newEmail);

        userService.changeEmail(userId, newEmail);
        return ResponseEntity.ok().build();
    }
}
