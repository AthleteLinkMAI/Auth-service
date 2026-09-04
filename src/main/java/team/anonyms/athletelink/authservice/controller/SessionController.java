package team.anonyms.athletelink.authservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team.anonyms.athletelink.authservice.dto.SessionData;
import team.anonyms.athletelink.authservice.service.SessionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/session")
public class SessionController {
    private final Logger logger = LoggerFactory.getLogger(SessionController.class);

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<SessionData>> getSessions(@AuthenticationPrincipal UUID userId) {
        logger.info("Called get-sessions; userId={}", userId);

        return ResponseEntity.ok(sessionService.getSessions(userId));
    }

    @DeleteMapping("/terminate")
    public ResponseEntity<Void> terminateSession(@RequestBody UUID sessionId, @AuthenticationPrincipal UUID userId) {
        logger.info("Called terminate-session; sessionId={}, userId={}", sessionId, userId);

        sessionService.terminateSession(sessionId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/terminate-all")
    public ResponseEntity<Void> terminateAllSessions(@AuthenticationPrincipal UUID userId) {
        logger.info("Called terminate-all-sessions; userId={}", userId);

        sessionService.terminateAllSessions(userId);
        return ResponseEntity.noContent().build();
    }
}
