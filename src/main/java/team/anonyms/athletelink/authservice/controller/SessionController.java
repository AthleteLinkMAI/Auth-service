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
    public ResponseEntity<List<SessionControllerDto>> getSessions(@PathVariable UUID userId) {
        log.info("Called get-sessions; userId={}", userId);

        return ResponseEntity.ok(sessionService.getSessions(userId));
    }

    @DeleteMapping("/terminate/{userId}")
    public ResponseEntity<Void> terminateSession(@RequestBody UUID sessionId, UUID userId) {
        log.info("Called terminate-session; sessionId={}, userId={}", sessionId, userId);

        TerminateSessionRequestControllerDto controllerDto = new TerminateSessionRequestControllerDto(sessionId, userId);
        sessionService.terminateSession(sessionMapper.terminateSessionRequestControllerToServiceDto(controllerDto));

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/terminate-all/{userId}")
    public ResponseEntity<Void> terminateAllSessions(UUID userId) {
        log.info("Called terminate-all-sessions; userId={}", userId);

        sessionService.terminateAllSessions(userId);
        return ResponseEntity.noContent().build();
    }
}
