package team.anonyms.athletelink.authservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.anonyms.athletelink.authservice.dto.controller.user.ChangePasswordRequestControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.user.RegisterRequestControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.user.UserControllerDto;
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

    @GetMapping("/me/{userId}")
    public ResponseEntity<UserControllerDto> me(@PathVariable UUID userId) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "GET /user/me/{userId}")
                .addKeyValue("userId", userId)
                .log();

        return ResponseEntity.ok(userMapper.userServiceToControllerDto(userService.me(userId)));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody @Valid RegisterRequestControllerDto registerRequestControllerDto
    ) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "POST /user/register")
                .log();

        userService.register(userMapper.registerControllerToServiceDto(registerRequestControllerDto));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/update-user/{userId}")
    public ResponseEntity<Void> updateUser(@RequestBody @Valid UserUpdateControllerDto dto, @PathVariable UUID userId) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "PUT /user/update-user/{userId}")
                .addKeyValue("userId", userId)
                .log();

        userService.updateUser(userMapper.userUpdateControllerToServiceDto(userId, dto));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password/{userId}")
    public ResponseEntity<Void> changePassword(
            @RequestBody @Valid ChangePasswordRequestControllerDto changePasswordRequestControllerDto,
            @PathVariable UUID userId
    ) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "PUT /user/change-password/{userId}")
                .addKeyValue("userId", userId)
                .log();

        userService.changePassword(
                userMapper.changePasswordRequestControllerToServiceDto(userId, changePasswordRequestControllerDto)
        );

        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-email/{userId}")
    public ResponseEntity<Void> changeEmail(@RequestBody @Email String newEmail, @PathVariable UUID userId) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "PUT /user/change-email/{userId}")
                .addKeyValue("userId", userId)
                .log();

        userService.changeEmail(userId, newEmail);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "DELETE /user/delete/{userId}")
                .addKeyValue("userId", userId)
                .log();

        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
