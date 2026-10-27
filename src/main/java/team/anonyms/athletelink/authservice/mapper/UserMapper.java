package team.anonyms.athletelink.authservice.mapper;

import org.mapstruct.Mapper;
import team.anonyms.athletelink.authservice.dto.controller.user.*;
import team.anonyms.athletelink.authservice.dto.service.user.*;

import java.util.List;
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

    List<UserPublicControllerDto> UserPublicServiceToControllerDtoList(List<UserPublicServiceDto> users);
}
