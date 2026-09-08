package team.anonyms.athletelink.authservice.mapper;

import org.springframework.stereotype.Component;
import team.anonyms.athletelink.authservice.dto.controller.session.SessionControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.session.TerminateSessionRequestControllerDto;
import team.anonyms.athletelink.authservice.dto.service.session.SessionServiceDto;
import team.anonyms.athletelink.authservice.dto.service.session.TerminateSessionRequestServiceDto;

import java.util.LinkedList;
import java.util.List;

@Component
public class SessionMapper {
    public SessionControllerDto sessionServiceToControllerDto(SessionServiceDto sessionServiceDto) {
        return new SessionControllerDto(
                sessionServiceDto.sessionId(),
                sessionServiceDto.expiration(),
                sessionServiceDto.lastUsed(),
                sessionServiceDto.userAgent(),
                sessionServiceDto.ipAddress()
        );
    }

    public List<SessionControllerDto> sessionServiceListToControllerListDto(
            List<SessionServiceDto> sessionServiceDtoList
    ) {
        List<SessionControllerDto> sessionControllerDtoList = new LinkedList<>();

        for (SessionServiceDto sessionServiceDto : sessionServiceDtoList) {
            sessionControllerDtoList.add(sessionServiceToControllerDto(sessionServiceDto));
        }

        return sessionControllerDtoList;
    }

    public TerminateSessionRequestServiceDto terminateSessionRequestControllerToServiceDto(
            TerminateSessionRequestControllerDto terminateSessionRequestControllerDto
    ) {
        return new TerminateSessionRequestServiceDto(
                terminateSessionRequestControllerDto.sessionId(),
                terminateSessionRequestControllerDto.userId()
        );
    }
}
