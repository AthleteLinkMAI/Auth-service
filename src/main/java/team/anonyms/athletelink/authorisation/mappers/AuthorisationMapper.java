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

    public LoginResultControllerDto loginResultServiceToControllerDto(LoginResultServiceDto loginResultServiceDto) {
        return new LoginResultControllerDto(
                loginResultServiceDto.success(),
                loginResultServiceDto.isVerified(),
                loginResultServiceDto.userId()
        );
    }

    public RegisterRequestServiceDto registerControllerToServiceDto(RegisterRequestControllerDto registerRequestServiceDto) {
        return new RegisterRequestServiceDto(
                registerRequestServiceDto.username(),
                registerRequestServiceDto.email(),
                registerRequestServiceDto.password()
        );
    }
}
