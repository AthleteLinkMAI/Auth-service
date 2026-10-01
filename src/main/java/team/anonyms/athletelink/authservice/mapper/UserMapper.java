package team.anonyms.athletelink.authservice.mapper;

import org.springframework.stereotype.Component;
import team.anonyms.athletelink.authservice.dto.controller.user.ChangePasswordRequestControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.user.RegisterRequestControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.user.UserControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.user.UserUpdateControllerDto;
import team.anonyms.athletelink.authservice.dto.service.user.ChangePasswordRequestServiceDto;
import team.anonyms.athletelink.authservice.dto.service.user.RegisterRequestServiceDto;
import team.anonyms.athletelink.authservice.dto.service.user.UserServiceDto;
import team.anonyms.athletelink.authservice.dto.service.user.UserUpdateServiceDto;

import java.util.UUID;

@Component
public class UserMapper {
    public UserControllerDto userServiceToControllerDto(UserServiceDto updateServiceDto) {
        return new UserControllerDto(

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

    public UserUpdateServiceDto userUpdateControllerToServiceDto(
            UUID userId,
            UserUpdateControllerDto userUpdateControllerDto
    ) {
        return new UserUpdateServiceDto(
                userId,
                userUpdateControllerDto.username()
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
}
