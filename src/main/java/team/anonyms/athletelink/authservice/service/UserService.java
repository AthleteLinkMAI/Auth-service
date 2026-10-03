package team.anonyms.athletelink.authservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.anonyms.athletelink.authservice.dto.service.kafka.UserChange;
import team.anonyms.athletelink.authservice.dto.service.kafka.enums.UserChangeType;
import team.anonyms.athletelink.authservice.dto.service.user.ChangePasswordRequestServiceDto;
import team.anonyms.athletelink.authservice.dto.service.user.RegisterRequestServiceDto;
import team.anonyms.athletelink.authservice.dto.service.user.UserServiceDto;
import team.anonyms.athletelink.authservice.dto.service.user.UserUpdateServiceDto;
import team.anonyms.athletelink.authservice.entity.User;
import team.anonyms.athletelink.authservice.exception.EmailAlreadyExistsException;
import team.anonyms.athletelink.authservice.exception.IncorrectPasswordException;
import team.anonyms.athletelink.authservice.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final KafkaProducerService kafkaProducerService;
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceDto me(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User was not found; id=" + userId)
        );

        return new UserServiceDto(
                user.getUsername(),
                user.getEmail(),
                user.getIsVerified()
        );
    }

    /**
     * Registers new user in the system.
     *
     * @param registerRequestServiceDto credentials to register.
     * @throws IllegalArgumentException if specified email already exists in DB.
     */
    @Transactional
    public void register(RegisterRequestServiceDto registerRequestServiceDto) {
        String email = registerRequestServiceDto.email();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException("User with this email already exists; email={}" + email);
        }

        User userToRegister = User.builder()
                .username(registerRequestServiceDto.username())
                .email(email)
                .password(passwordEncoder.encode(registerRequestServiceDto.password()))
                .isVerified(false)
                .build();

        User userRegistered = userRepository.save(userToRegister);
        kafkaProducerService.sendUserChange(
                new UserChange(UserChangeType.CREATE, userRegistered.getId())
        );
    }

    @Transactional
    public void updateUser(UserUpdateServiceDto userUpdateServiceDto) {
        User userToUpdate = userRepository.findById(userUpdateServiceDto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found; id=" + userUpdateServiceDto.userId()));

        userToUpdate.setUsername(userUpdateServiceDto.username());
        userRepository.save(userToUpdate);
    }

    @Transactional
    public void changePassword(ChangePasswordRequestServiceDto changePasswordRequestServiceDto) {
        User userToChangePassword = userRepository.findById(changePasswordRequestServiceDto.userId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found; id=" + changePasswordRequestServiceDto.userId())
                );

        if (!passwordEncoder.matches(changePasswordRequestServiceDto.oldPassword(), userToChangePassword.getPassword())) {
            throw new IncorrectPasswordException("Passwords don't match");
        }

        userToChangePassword.setPassword(passwordEncoder.encode(changePasswordRequestServiceDto.newPassword()));
        userRepository.save(userToChangePassword);
    }

    @Transactional
    public void changeEmail(UUID userId, String newEmail) {
        User userToChangeEmail = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found; id=" + userId));

        userToChangeEmail.setEmail(newEmail);
        userToChangeEmail.setIsVerified(Boolean.FALSE);
        userRepository.save(userToChangeEmail);
    }

    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            return;
        }

        userRepository.deleteById(userId);

        kafkaProducerService.sendUserChange(
                new UserChange(UserChangeType.DELETE, userId)
        );
    }
}
