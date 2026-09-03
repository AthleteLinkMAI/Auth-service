package team.anonyms.athletelink.authservice.mapper;

import org.springframework.stereotype.Component;
import team.anonyms.athletelink.authservice.dto.controller.auth.ChangePasswordRequestControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.auth.CredentialsControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.auth.LoginResultControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.auth.RegisterRequestControllerDto;
import team.anonyms.athletelink.authservice.dto.service.auth.ChangePasswordRequestServiceDto;
import team.anonyms.athletelink.authservice.dto.service.auth.CredentialsServiceDto;
import team.anonyms.athletelink.authservice.dto.service.auth.LoginResultServiceDto;
import team.anonyms.athletelink.authservice.dto.service.auth.RegisterRequestServiceDto;

import java.util.UUID;

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

    public ChangePasswordRequestServiceDto changePasswordRequestControllerToServiceDto(
            UUID userId,
            ChangePasswordRequestControllerDto changePasswordRequestControllerDto
    ) {
        return new ChangePasswordRequestServiceDto(
                userId,
                changePasswordRequestControllerDto.oldPassword(),
                changePasswordRequestControllerDto.newPassword()
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
