package team.anonyms.athletelink.authservice.mapper;

import org.mapstruct.Mapper;
import team.anonyms.athletelink.authservice.dto.controller.ban.BanControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.ban.BanToCreateControllerDto;
import team.anonyms.athletelink.authservice.dto.service.ban.BanServiceDto;
import team.anonyms.athletelink.authservice.dto.service.ban.BanToCreateServiceDto;
import team.anonyms.athletelink.authservice.entity.UserCredentials;

import java.util.UUID;

@Mapper
public interface BanMapper {
    BanToCreateServiceDto banToCreateControllerToServiceDto(
            UUID userId,
            UserCredentials.UserRoleType role,
            BanToCreateControllerDto dto
    );

    BanControllerDto banServiceToControllerDto(BanServiceDto dto);
}
