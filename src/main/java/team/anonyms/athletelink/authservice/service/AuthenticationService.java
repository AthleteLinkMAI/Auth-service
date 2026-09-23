package team.anonyms.athletelink.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import team.anonyms.athletelink.authservice.dto.service.auth.CredentialsServiceDto;
import team.anonyms.athletelink.authservice.dto.service.auth.LoginResultServiceDto;
import team.anonyms.athletelink.authservice.entity.User;
import team.anonyms.athletelink.authservice.repository.UserRepository;

import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static team.anonyms.athletelink.authservice.security.filters.JwtAuthenticationFilter.JWT_DENYLIST_KEY_PREFIX;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final StringRedisTemplate stringRedisTemplate;

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
     * Logging out current device from the system.
     * 
     * @param refreshToken token for deletion.
     */
    public void logout(String refreshToken) {
        jwtService.deleteRefreshToken(refreshToken);

        stringRedisTemplate.opsForValue().set(JWT_DENYLIST_KEY_PREFIX + refreshToken, "", 5, TimeUnit.MINUTES);
    }
}
