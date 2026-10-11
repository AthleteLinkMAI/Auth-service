package team.anonyms.athletelink.authservice.mapper;

import org.mapstruct.Mapper;
import team.anonyms.athletelink.authservice.dto.controller.verification.EmailVerificationControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.verification.PasswordResetRequestControllerDto;
import team.anonyms.athletelink.authservice.dto.service.verification.EmailVerificationServiceDto;
import team.anonyms.athletelink.authservice.dto.service.verification.PasswordResetRequestServiceDto;

@Mapper
public interface VerificationMapper {
    EmailVerificationServiceDto emailVerificationControllerToServiceDto(
            EmailVerificationControllerDto emailVerificationControllerDto
    );

    PasswordResetRequestServiceDto passwordResetRequestControllerToServiceDto(
            PasswordResetRequestControllerDto passwordResetRequestControllerDto
    );
}
