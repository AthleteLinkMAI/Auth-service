package team.anonyms.athletelink.authorisation.mappers;

import org.springframework.stereotype.Component;
import team.anonyms.athletelink.authorisation.dto.controller.authorisation.CredentialsControllerDto;
import team.anonyms.athletelink.authorisation.dto.controller.authorisation.LoginResultControllerDto;
import team.anonyms.athletelink.authorisation.dto.controller.authorisation.RegisterRequestControllerDto;
import team.anonyms.athletelink.authorisation.dto.service.authorisation.CredentialsServiceDto;
import team.anonyms.athletelink.authorisation.dto.service.authorisation.LoginResultServiceDto;
import team.anonyms.athletelink.authorisation.dto.service.authorisation.RegisterRequestServiceDto;

@Component
public class AuthorisationMapper {
    public CredentialsServiceDto credentialsControllerToServiceDto(
            CredentialsControllerDto credentialsControllerDto,
            String userAgent,
            String userIpAddress
    ) {
        return new CredentialsServiceDto(
                credentialsControllerDto.email(),
                credentialsControllerDto.password(),
                userAgent,
                userIpAddress
        );
    }

    public RegisterRequestServiceDto registerControllerToServiceDto(
            RegisterRequestControllerDto registerRequestControllerDto
    ) {
        return new RegisterRequestServiceDto(
                registerRequestControllerDto.username(),
                registerRequestControllerDto.email(),
                registerRequestControllerDto.password()
        );
    }

    public LoginResultControllerDto loginResultServiceToControllerDto(LoginResultServiceDto loginResultServiceDto) {
        return new LoginResultControllerDto(
                loginResultServiceDto.success(),
                loginResultServiceDto.isVerified(),
                loginResultServiceDto.userId(),
                loginResultServiceDto.refreshToken()
        );
    }
}
