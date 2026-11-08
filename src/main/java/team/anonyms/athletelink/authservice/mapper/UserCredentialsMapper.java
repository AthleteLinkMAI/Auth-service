package team.anonyms.athletelink.authservice.mapper;

import org.mapstruct.Mapper;
import team.anonyms.athletelink.authservice.dto.controller.user.*;
import team.anonyms.athletelink.authservice.dto.service.user.*;

import java.util.UUID;

@Mapper
public interface UserCredentialsMapper {
    UpdateRoleRequestServiceDto updateRoleRequestControllerToServiceDto(UpdateRoleRequestControllerDto dto);

    UserCredentialsControllerDto userServiceToControllerDto(UserCredentialsServiceDto updateServiceDto);

    RegisterRequestServiceDto registerControllerToServiceDto(
            RegisterRequestControllerDto registerRequestControllerDto
    );

    ChangePasswordRequestServiceDto changePasswordRequestControllerToServiceDto(
            UUID userId,
            ChangePasswordRequestControllerDto changePasswordRequestControllerDto
    );
}
