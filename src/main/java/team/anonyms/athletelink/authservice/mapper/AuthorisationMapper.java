package team.anonyms.athletelink.authservice.mapper;

import org.springframework.stereotype.Component;
import team.anonyms.athletelink.authservice.dto.controller.auth.CredentialsControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.auth.LoginResultControllerDto;
import team.anonyms.athletelink.authservice.dto.service.auth.CredentialsServiceDto;
import team.anonyms.athletelink.authservice.dto.service.auth.LoginResultServiceDto;

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
                loginResultServiceDto.userId(),
                loginResultServiceDto.refreshToken()
        );
    }
}
