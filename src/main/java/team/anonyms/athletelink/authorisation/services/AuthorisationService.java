package team.anonyms.athletelink.authorisation.services;

import team.anonyms.athletelink.authorisation.dto.DeviceData;
import team.anonyms.athletelink.authorisation.dto.service.authorisation.CredentialsServiceDto;
import team.anonyms.athletelink.authorisation.dto.service.authorisation.LoginResultServiceDto;
import team.anonyms.athletelink.authorisation.entities.JwtRefreshToken;
import team.anonyms.athletelink.authorisation.entities.User;
import team.anonyms.athletelink.authorisation.repositories.UserRepository;
import team.anonyms.athletelink.authorisation.dto.service.authorisation.RegisterRequestServiceDto;

import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
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
     * Registers new user in the system.
     *
     * @param dto credentials to register
     * @return user's UUID
     * @throws IllegalArgumentException if specified email already exists in DB
     */
    @Transactional
    public UUID register(@NonNull RegisterRequestServiceDto dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            // TODO: add custom Exception
            throw new IllegalArgumentException("User with this email already exists.");
        }

        String encodedPassword = passwordEncoder.encode(dto.password());

        User newUser = User.builder()
                .username(dto.username())
                .email(dto.email())
                .password(encodedPassword)
                .isVerified(false) 
                .build();

        User savedUser = userRepository.save(newUser);
        return savedUser.getId();
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

        if (userOptional.isEmpty() || !passwordEncoder.matches(credentialsServiceDto.password(), userOptional.get().getPassword())) {
            return new LoginResultServiceDto(false, null, null, null, null);
        }

        User user = userOptional.get();

        String accessToken = jwtService.generateAccessToken(user);

        String refreshToken = jwtService.createOrUpdateRefreshToken(
                user,
                credentialsServiceDto.userAgent(),
                credentialsServiceDto.userIpAddress()
        );

        return new LoginResultServiceDto(
                true,
                user.getIsVerified(),
                user.getId(),
                accessToken,
                refreshToken
        );
    }

    /**
     * Logging out of system.
     * 
     * @param refreshToken token that must be invalidated
     */
    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            jwtService.deleteRefreshToken(refreshToken);
        }
    }

    /**
     * Sets user's new password.
     * @param email user's email
     * @param newRawPassword password (is set by force)
     */
    @Transactional
    public void resetPassword(String email, String newRawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String encodedPassword = passwordEncoder.encode(newRawPassword);
        user.setPassword(encodedPassword);
        
        userRepository.save(user);
    }

    public String updateAccessToken(String refreshToken) {
        return jwtService.refreshAccessToken(refreshToken);
    }
}
