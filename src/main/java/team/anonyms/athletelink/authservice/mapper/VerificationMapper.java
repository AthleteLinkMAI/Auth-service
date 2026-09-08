package team.anonyms.athletelink.authservice.mapper;

import org.springframework.stereotype.Component;
import team.anonyms.athletelink.authservice.dto.controller.verification.EmailVerificationControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.verification.PasswordResetRequestControllerDto;
import team.anonyms.athletelink.authservice.dto.service.verification.EmailVerificationServiceDto;
import team.anonyms.athletelink.authservice.dto.service.verification.PasswordResetRequestServiceDto;

@Component
public class VerificationMapper {
    public EmailVerificationServiceDto emailVerificationControllerToServiceDto(
            EmailVerificationControllerDto emailVerificationControllerDto
    ) {
        return new EmailVerificationServiceDto(
                emailVerificationControllerDto.emailVerificationCode(),
                emailVerificationControllerDto.userId()
        );
    }

    public PasswordResetRequestServiceDto passwordResetRequestServiceToControllerDto(
            PasswordResetRequestControllerDto passwordResetRequestControllerDto
    ) {
        return new PasswordResetRequestServiceDto(
                passwordResetRequestControllerDto.email(),
                passwordResetRequestControllerDto.code()
        );
    }
}
