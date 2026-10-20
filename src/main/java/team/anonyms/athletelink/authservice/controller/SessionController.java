package team.anonyms.athletelink.authservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.anonyms.athletelink.authservice.dto.controller.session.SessionControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.session.TerminateSessionRequestControllerDto;
import team.anonyms.athletelink.authservice.mapper.SessionMapper;
import team.anonyms.athletelink.authservice.service.SessionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
@Slf4j
public class SessionController {
    private final SessionService sessionService;
    private final SessionMapper sessionMapper;

    @GetMapping("/get-all/{userId}")
    public ResponseEntity<List<SessionControllerDto>> getAllSessions(@PathVariable UUID userId) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "GET /session/get-all/{userId}")
                .addKeyValue("userId", userId)
                .log();

        return ResponseEntity.ok(sessionService.getSessions(userId));
    }

    @DeleteMapping("/terminate/{userId}")
    public ResponseEntity<Void> terminateSession(@RequestBody UUID sessionId, @PathVariable UUID userId) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "DELETE /session/terminate/{userId}")
                .addKeyValue("userId", userId)
                .addKeyValue("sessionId", sessionId)
                .log();

        TerminateSessionRequestControllerDto controllerDto = new TerminateSessionRequestControllerDto(sessionId, userId);
        sessionService.terminateSession(sessionMapper.terminateSessionRequestControllerToServiceDto(controllerDto));

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/terminate-all/{userId}")
    public ResponseEntity<Void> terminateAllSessions(@PathVariable UUID userId) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "DELETE /session/terminate-all/{userId}")
                .addKeyValue("userId", userId)
                .log();

        sessionService.terminateAllSessions(userId);
        return ResponseEntity.noContent().build();
    }
}
