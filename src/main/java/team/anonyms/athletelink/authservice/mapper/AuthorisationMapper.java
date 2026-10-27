package team.anonyms.athletelink.authservice.mapper;

import org.mapstruct.Mapper;
import team.anonyms.athletelink.authservice.dto.controller.auth.CredentialsControllerDto;
import team.anonyms.athletelink.authservice.dto.service.auth.CredentialsServiceDto;

@Mapper
public interface AuthorisationMapper {
    CredentialsServiceDto credentialsControllerToServiceDto(
            CredentialsControllerDto credentialsControllerDto,
            String userAgent,
            String userIpAddress
    );

}
