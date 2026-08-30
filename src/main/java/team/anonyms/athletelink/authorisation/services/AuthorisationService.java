package team.anonyms.athletelink.authorisation.services;

import jakarta.persistence.EntityNotFoundException;
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

    // Move to GET /sessions
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
     * @param credentialsServiceDto a user's credentials.
     * @return {@link LoginResultServiceDto}.
     */
    public LoginResultServiceDto login(@NonNull CredentialsServiceDto credentialsServiceDto) {
        Optional<User> userOptional = userRepository.findByEmail(credentialsServiceDto.email());

        if (userOptional.isEmpty() || !passwordEncoder.matches(credentialsServiceDto.password(), userOptional.get().getPassword())) {
            return new LoginResultServiceDto(false, null, null, null);
        }

        User user = userOptional.get();
        String refreshToken = jwtService.createOrUpdateRefreshToken(
                user,
                credentialsServiceDto.userAgent(),
                credentialsServiceDto.userIpAddress()
        );

        return new LoginResultServiceDto(
                true,
                user.getIsVerified(),
                user.getId(),
                refreshToken
        );
    }

    /**
     * Registers new user in the system.
     *
     * @param registerDto credentials to register.
     * @return user's UUID.
     * @throws IllegalArgumentException if specified email already exists in DB.
     */
    @Transactional
    public UUID register(@NonNull RegisterRequestServiceDto registerDto) {
        if (userRepository.findByEmail(registerDto.email()).isPresent()) {
            // TODO: add custom Exception
            throw new IllegalArgumentException("User with this email already exists.");
        }

        User userToRegister = User.builder()
                .username(registerDto.username())
                .email(registerDto.email())
                .password(passwordEncoder.encode(registerDto.password()))
                .isVerified(false) 
                .build();

        User userRegistered = userRepository.save(userToRegister);
        return userRegistered.getId();
    }

    /**
     * Logging out of system.
     * 
     * @param refreshToken token for deletion.
     */
    public void logout(String refreshToken) {
        jwtService.deleteRefreshToken(refreshToken);
    }

    /**
     * Sets user's new password.
     * @param email user's email.
     * @param newRawPassword new password.
     */
    // TODO: add verification by sending an email code
    @Transactional
    public void resetPassword(String email, String newRawPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );

        String encodedPassword = passwordEncoder.encode(newRawPassword);
        user.setPassword(encodedPassword);
        
        userRepository.save(user);
    }

    /**
     * Change password for authorised user.
     */
    @Transactional
    public void changePassword(UUID userId, String oldRawPassword, String newRawPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (!passwordEncoder.matches(oldRawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password.");
        }

        user.setPassword(passwordEncoder.encode(newRawPassword));
        userRepository.save(user);
    }
}
