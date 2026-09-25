package team.anonyms.athletelink.authservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.anonyms.athletelink.authservice.dto.controller.session.SessionControllerDto;
import team.anonyms.athletelink.authservice.dto.service.session.SessionServiceDto;
import team.anonyms.athletelink.authservice.dto.service.session.TerminateSessionRequestServiceDto;
import team.anonyms.athletelink.authservice.entity.JwtRefreshToken;
import team.anonyms.athletelink.authservice.exception.InsecureDirectObjectReferenceException;
import team.anonyms.athletelink.authservice.mapper.SessionMapper;
import team.anonyms.athletelink.authservice.repository.JwtRefreshTokenRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final JwtRefreshTokenRepository jwtRefreshTokenRepository;
    private final SessionMapper sessionMapper;

    public static List<SessionServiceDto> createSessionDataListFromJwtRefreshTokenList(
            List<JwtRefreshToken> jwtRefreshTokenList
    ) {
        List<SessionServiceDto> sessionControllerDtoList = new ArrayList<>();

        for (JwtRefreshToken jwtRefreshToken : jwtRefreshTokenList) {
            sessionControllerDtoList.add(
                    new SessionServiceDto(
                            jwtRefreshToken.getJti(),
                            jwtRefreshToken.getExpiration(),
                            jwtRefreshToken.getLastUsed(),
                            jwtRefreshToken.getUserAgent(),
                            jwtRefreshToken.getIpAddress()
                    )
            );
        }

        return sessionControllerDtoList;
    }

    public List<SessionControllerDto> getSessions(UUID userId) {
        return sessionMapper.sessionServiceListToControllerListDto(
                createSessionDataListFromJwtRefreshTokenList(jwtRefreshTokenRepository.findAllByUserId(userId))
        );
    }

    @Transactional
    public void terminateSession(TerminateSessionRequestServiceDto terminateSessionDto) {
        JwtRefreshToken activeSession = jwtRefreshTokenRepository.findById(terminateSessionDto.sessionId()).orElseThrow(
                () -> new EntityNotFoundException("Session not found; sessionId=" + terminateSessionDto.sessionId())
        );

        if (!activeSession.getUser().getId().equals(terminateSessionDto.userId())) {
            throw new InsecureDirectObjectReferenceException(
                    "IDOR occurred; jwtRefreshTokenId=" + terminateSessionDto.sessionId() + ", actual userId=" +
                            activeSession.getUser().getId() + ", authenticated userId=" + terminateSessionDto.userId()
            );
        }

        jwtRefreshTokenRepository.delete(activeSession);
    }

    @Transactional
    public void terminateAllSessions(UUID userId) {
        jwtRefreshTokenRepository.deleteAll(jwtRefreshTokenRepository.findAllByUserId(userId));
    }
}
