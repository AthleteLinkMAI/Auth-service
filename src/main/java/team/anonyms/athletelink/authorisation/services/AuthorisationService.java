package team.anonyms.athletelink.authorisation.services;

import team.anonyms.athletelink.authorisation.dto.DeviceData;
import team.anonyms.athletelink.authorisation.dto.service.authorisation.CredentialsServiceDto;
import team.anonyms.athletelink.authorisation.dto.service.authorisation.LoginResultServiceDto;
import team.anonyms.athletelink.authorisation.entities.JwtRefreshToken;
import team.anonyms.athletelink.authorisation.entities.User;
import team.anonyms.athletelink.authorisation.repositories.UserRepository;

import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorisationService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthorisationService(
            JwtService jwtService,
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private static final LoginResultServiceDto UNSUCCESSFUL_LOGIN_RESULT_SERVICE_DTO = new LoginResultServiceDto(
            Boolean.FALSE,
            null,
            null
    );

    // Move to new API endpoint
    public static List<DeviceData> createDeviceDataListFromJwtRefreshTokenList(
            List<JwtRefreshToken> jwtRefreshTokenList
    ) {
        List<DeviceData> deviceDataList = new ArrayList<>();

        for (JwtRefreshToken jwtRefreshToken : jwtRefreshTokenList) {
            deviceDataList.add(
                    new DeviceData(
                            jwtRefreshToken.getDeviceId(),
                            jwtRefreshToken.getExpiration(),
                            jwtRefreshToken.getLastUsed(),
                            jwtRefreshToken.getUserAgent(),
                            jwtRefreshToken.getIpAddress()
                    )
            );
        }

        return deviceDataList;
    }

    /**
     * Login a user into system.
     *
     * @param credentialsServiceDto a user's credentials
     * @return {@link LoginResultServiceDto} if login is successful or null if login is unsuccessful.
     *
     * @throws IllegalArgumentException if password is incorrect for encoding.
     */
    public LoginResultServiceDto login(@NonNull CredentialsServiceDto credentialsServiceDto) {
        Optional<User> userOptional = userRepository.findByEmail(credentialsServiceDto.email());
        if (userOptional.isEmpty()) {
            return UNSUCCESSFUL_LOGIN_RESULT_SERVICE_DTO;
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(credentialsServiceDto.password(), user.getPassword())) {
            return UNSUCCESSFUL_LOGIN_RESULT_SERVICE_DTO;
        }

        jwtService.addRefreshToken(
                user,
                credentialsServiceDto.userAgent(),
                credentialsServiceDto.userIpAddress()
        );

        return new LoginResultServiceDto(
                Boolean.TRUE,
                user.getIsVerified(),
                user.getId()
        );
    }

    public Void updateAccessToken(String accessToken) {
        return null;
    }
}
