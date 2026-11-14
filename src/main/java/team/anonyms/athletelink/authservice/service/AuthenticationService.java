package team.anonyms.athletelink.authservice.service;

import lombok.RequiredArgsConstructor;
import team.anonyms.athletelink.authservice.dto.service.auth.CredentialsServiceDto;
import team.anonyms.athletelink.authservice.dto.service.auth.LoginResultDto;
import team.anonyms.athletelink.authservice.entity.UserCredentials;
import team.anonyms.athletelink.authservice.exception.AccessForbiddenException;
import team.anonyms.athletelink.authservice.repository.BanRepository;
import team.anonyms.athletelink.authservice.repository.UserCredentialsRepository;

import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtService jwtService;
    private final RedisService redisService;
    private final UserCredentialsRepository userCredentialsRepository;
    private final BanRepository banRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Login a user into system.
     *
     * @param credentialsServiceDto a user's credentials.
     * @return {@link LoginResultDto}.
     */
    public LoginResultDto login(@NonNull CredentialsServiceDto credentialsServiceDto) {
        Optional<UserCredentials> userOptional = userCredentialsRepository.findByEmail(credentialsServiceDto.email());

        if (userOptional.isEmpty() ||
                !passwordEncoder.matches(credentialsServiceDto.password(), userOptional.get().getPassword())) {
            return new LoginResultDto(false, null);
        }

        UserCredentials userCredentials = userOptional.get();
        if (banRepository.findById(userCredentials.getUserId()).isPresent()) {
            throw new AccessForbiddenException("User is banned");
        }

        String refreshToken = jwtService.createOrUpdateRefreshToken(
                userCredentials,
                credentialsServiceDto.userAgent(),
                credentialsServiceDto.userIpAddress()
        );

        return new LoginResultDto(true, refreshToken);
    }

    /**
     * Logging out current device from the system.
     * 
     * @param refreshToken refreshToken for deletion.
     * @param accessToken accessToken for including in denylist.
     */
    public void logout(String refreshToken, String accessToken) {
        jwtService.deleteRefreshToken(refreshToken);
        redisService.addJwtAccessTokenToDenylist(jwtService.extractId(accessToken));
    }
}
