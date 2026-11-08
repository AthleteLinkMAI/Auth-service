package team.anonyms.athletelink.authservice.controller;

import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.anonyms.athletelink.authservice.dto.controller.ban.BanControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.ban.BanToCreateControllerDto;
import team.anonyms.athletelink.authservice.entity.UserCredentials;
import team.anonyms.athletelink.authservice.mapper.BanMapper;
import team.anonyms.athletelink.authservice.service.BanService;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/ban")
public class BanController {
    private final BanService banService;
    private final BanMapper banMapper;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{email}")
    public BanControllerDto getBanByUserEmail(@RequestPart @Email String email) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "GET /ban/{email}")
                .addKeyValue("email", email)
                .log();

        return banMapper.banServiceToControllerDto(banService.getBanByUserEmail(email));
    }

    @PostMapping
    public ResponseEntity<BanControllerDto> banUser(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") UserCredentials.UserRoleType role,
            @RequestBody BanToCreateControllerDto banToCreateControllerDto
    ) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "POST /ban")
                .addKeyValue("userId", userId)
                .addKeyValue("role", role)
                .addKeyValue("banToCreate", banToCreateControllerDto)
                .log();


        if (role.equals(UserCredentials.UserRoleType.USER)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.status(201).body(
                banMapper.banServiceToControllerDto(
                        banService.banUser(
                                banMapper.banToCreateControllerToServiceDto(userId, role, banToCreateControllerDto)
                        )
                )
        );
    }

    @DeleteMapping
    public ResponseEntity<Void> unbanUser(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") UserCredentials.UserRoleType role,
            @RequestBody UUID userToUnban
    ) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "DELETE /ban")
                .addKeyValue("userId", userId)
                .addKeyValue("role", role)
                .addKeyValue("userToUnban", userToUnban)
                .log();

        if (role.equals(UserCredentials.UserRoleType.USER)) {
            return ResponseEntity.status(403).build();
        }

        banService.unbanUser(userToUnban);
        return ResponseEntity.noContent().build();
    }
}
