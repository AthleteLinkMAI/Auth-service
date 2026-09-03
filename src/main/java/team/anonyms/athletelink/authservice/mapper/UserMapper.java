package team.anonyms.athletelink.authservice.mapper;

import org.springframework.stereotype.Component;
import team.anonyms.athletelink.authservice.dto.controller.user.UserUpdateControllerDto;
import team.anonyms.athletelink.authservice.dto.service.user.UserUpdateServiceDto;

import java.util.UUID;

@Component
public class UserMapper {
    public UserUpdateServiceDto userUpdateControllerToServiceDto(
            UUID userId,
            UserUpdateControllerDto userUpdateControllerDto
    ) {
        return new UserUpdateServiceDto(
                userId,
                userUpdateControllerDto.username()
        );
    }
}
