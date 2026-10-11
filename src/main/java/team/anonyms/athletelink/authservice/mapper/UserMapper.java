package team.anonyms.athletelink.authservice.mapper;

import org.mapstruct.Mapper;
import team.anonyms.athletelink.authservice.dto.controller.user.ChangePasswordRequestControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.user.RegisterRequestControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.user.UserControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.user.UserUpdateControllerDto;
import team.anonyms.athletelink.authservice.dto.service.user.ChangePasswordRequestServiceDto;
import team.anonyms.athletelink.authservice.dto.service.user.RegisterRequestServiceDto;
import team.anonyms.athletelink.authservice.dto.service.user.UserServiceDto;
import team.anonyms.athletelink.authservice.dto.service.user.UserUpdateServiceDto;

import java.util.UUID;

@Mapper
public interface UserMapper {
    UserControllerDto userServiceToControllerDto(UserServiceDto updateServiceDto);

    RegisterRequestServiceDto registerControllerToServiceDto(
            RegisterRequestControllerDto registerRequestControllerDto
    );

    UserUpdateServiceDto userUpdateControllerToServiceDto(
            UUID userId,
            UserUpdateControllerDto userUpdateControllerDto
    );

    ChangePasswordRequestServiceDto changePasswordRequestControllerToServiceDto(
            UUID userId,
            ChangePasswordRequestControllerDto changePasswordRequestControllerDto
    );
}
