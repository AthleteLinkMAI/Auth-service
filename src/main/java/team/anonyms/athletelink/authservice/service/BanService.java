package team.anonyms.athletelink.authservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.anonyms.athletelink.authservice.dto.service.ban.BanServiceDto;
import team.anonyms.athletelink.authservice.dto.service.ban.BanToCreateServiceDto;
import team.anonyms.athletelink.authservice.dto.service.kafka.NotificationMessage;
import team.anonyms.athletelink.authservice.dto.service.kafka.UserToUpdate;
import team.anonyms.athletelink.authservice.dto.service.kafka.enums.NotificationType;
import team.anonyms.athletelink.authservice.entity.Ban;
import team.anonyms.athletelink.authservice.entity.UserCredentials;
import team.anonyms.athletelink.authservice.exception.AccessForbiddenException;
import team.anonyms.athletelink.authservice.exception.EntityAlreadyExistsException;
import team.anonyms.athletelink.authservice.repository.BanRepository;
import team.anonyms.athletelink.authservice.repository.JwtRefreshTokenRepository;
import team.anonyms.athletelink.authservice.repository.UserCredentialsRepository;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BanService {
    private final RedisService redisService;
    private final KafkaProducerService kafkaProducerService;
    private final BanRepository banRepository;
    private final UserCredentialsRepository userCredentialsRepository;
    private final JwtRefreshTokenRepository jwtRefreshTokenRepository;

    public boolean isUserBanned(UserCredentials userCredentials) {
        return isUserBanned(userCredentials.getUserId());
    }

    public boolean isUserBanned(UUID userId) {
        return banRepository.findById(userId).isPresent();
    }

    public BanServiceDto getBanByUserEmail(String email) {
        UserCredentials userCredentials = userCredentialsRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User is not found; email=" + email)
        );

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        Ban ban = banRepository.findById(userCredentials.getUserId()).get();

        return new BanServiceDto(
                ban.getUserId(),
                ban.getExpiration(),
                ban.getReason(),
                ban.getBannedBy()
        );
    }

    @Transactional
    public BanServiceDto banUser(BanToCreateServiceDto dto) {
        UserCredentials userCredentials = userCredentialsRepository.findById(dto.userToBan()).orElseThrow(
                () -> new EntityNotFoundException("User credentials are not found; id=" + dto.userToBan())
        );

        if (banRepository.findById(dto.userToBan()).isPresent()) {
            throw new EntityAlreadyExistsException("User is already banned; id=" + dto.userToBan());
        }

        if (userCredentials.getRole().equals(UserCredentials.UserRoleType.ADMIN)) {
            throw new AccessForbiddenException("It is impossible to ban an admin; userRole=" + userCredentials.getRole());
        }

        Ban banCreated = Ban.builder()
                .expiration(dto.expiration())
                .reason(dto.reason())
                .bannedBy(dto.userId())
                .userCredentials(userCredentials)
                .build();

        banRepository.save(banCreated);
        jwtRefreshTokenRepository.deleteAllByUserId(dto.userToBan());
        redisService.addUserToBanlist(userCredentials);

        kafkaProducerService.sendUserToUpdate(
                new UserToUpdate(
                        userCredentials.getUserId(),
                        userCredentials.getRole(),
                        userCredentials.getEmail(),
                        userCredentials.getIsVerified(),
                        true
                )
        );

        kafkaProducerService.sendNotificationMessage(
                new NotificationMessage(
                        NotificationType.EMAIL,
                        userCredentials.getEmail(),
                        "Ban on AthleteLink",
                        "Your account was banned on AthleteLink for the following reason:\n" + dto.reason()
                ),
                userCredentials.getUserId()
        );

        return new BanServiceDto(
                banCreated.getUserId(),
                banCreated.getExpiration(),
                banCreated.getReason(),
                banCreated.getBannedBy()
        );
    }

    public void unbanUser(UUID userId) {
        UserCredentials userCredentials = userCredentialsRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User is not found; id=" + userId)
        );

        banRepository.deleteById(userCredentials.getUserId());
        redisService.removeUserFromBanlist(userCredentials);

        kafkaProducerService.sendUserToUpdate(
                new UserToUpdate(
                        userCredentials.getUserId(),
                        userCredentials.getRole(),
                        userCredentials.getEmail(),
                        userCredentials.getIsVerified(),
                        false
                )
        );

        kafkaProducerService.sendNotificationMessage(
                new NotificationMessage(
                        NotificationType.EMAIL,
                        userCredentials.getEmail(),
                        "Unban on AthleteLink",
                        "Your account was banned on AthleteLink"
                ),
                userCredentials.getUserId()
        );
    }
}
