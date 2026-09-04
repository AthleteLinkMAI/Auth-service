package team.anonyms.athletelink.authservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import team.anonyms.athletelink.authservice.dto.SessionData;
import team.anonyms.athletelink.authservice.entity.JwtRefreshToken;
import team.anonyms.athletelink.authservice.exception.InsecureDirectObjectReferenceException;
import team.anonyms.athletelink.authservice.repository.JwtRefreshTokenRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SessionService {
    private final JwtRefreshTokenRepository jwtRefreshTokenRepository;

    public SessionService(JwtRefreshTokenRepository jwtRefreshTokenRepository) {
        this.jwtRefreshTokenRepository = jwtRefreshTokenRepository;
    }

    public static List<SessionData> createSessionDataListFromJwtRefreshTokenList(
            List<JwtRefreshToken> jwtRefreshTokenList
    ) {
        List<SessionData> sessionDataList = new ArrayList<>();

        for (JwtRefreshToken jwtRefreshToken : jwtRefreshTokenList) {
            sessionDataList.add(
                    new SessionData(
                            jwtRefreshToken.getId(),
                            jwtRefreshToken.getExpiration(),
                            jwtRefreshToken.getLastUsed(),
                            jwtRefreshToken.getUserAgent(),
                            jwtRefreshToken.getIpAddress()
                    )
            );
        }

        return sessionDataList;
    }

    public List<SessionData> getSessions(UUID userId) {
        return createSessionDataListFromJwtRefreshTokenList(jwtRefreshTokenRepository.findAllByUserId(userId));
    }

    @Transactional
    public void terminateSession(UUID sessionId, UUID userId) {
        JwtRefreshToken activeSession = jwtRefreshTokenRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found; sessionId=" + sessionId));

        if (!activeSession.getUser().getId().equals(userId)) {
            throw new InsecureDirectObjectReferenceException(
                    "IDOR occurred; jwtRefreshTokenId=" + sessionId + ", actual userId=" +
                            activeSession.getUser().getId() + ", authenticated userId=" + userId
            );
        }

        jwtRefreshTokenRepository.delete(activeSession);
    }

    @Transactional
    public void terminateAllSessions(UUID userId) {
        jwtRefreshTokenRepository.deleteAll(jwtRefreshTokenRepository.findAllByUserId(userId));
    }
}
