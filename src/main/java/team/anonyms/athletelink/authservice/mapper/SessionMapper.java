package team.anonyms.athletelink.authservice.mapper;

import org.mapstruct.Mapper;
import team.anonyms.athletelink.authservice.dto.controller.session.SessionControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.session.TerminateSessionRequestControllerDto;
import team.anonyms.athletelink.authservice.dto.service.session.SessionServiceDto;
import team.anonyms.athletelink.authservice.dto.service.session.TerminateSessionRequestServiceDto;

import java.util.List;

@Mapper
public interface SessionMapper {
    SessionControllerDto sessionServiceToControllerDto(SessionServiceDto sessionServiceDto);

    List<SessionControllerDto> sessionServiceListToControllerListDto(List<SessionServiceDto> sessionServiceDtoList);

    TerminateSessionRequestServiceDto terminateSessionRequestControllerToServiceDto(
            TerminateSessionRequestControllerDto terminateSessionRequestControllerDto
    );
}
