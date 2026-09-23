package team.anonyms.athletelink.authservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import team.anonyms.athletelink.authservice.dto.service.user.ChangePasswordRequestServiceDto;
import team.anonyms.athletelink.authservice.dto.service.user.RegisterRequestServiceDto;
import team.anonyms.athletelink.authservice.dto.service.user.UserUpdateServiceDto;
import team.anonyms.athletelink.authservice.entity.User;
import team.anonyms.athletelink.authservice.exception.EmailAlreadyExistsException;
import team.anonyms.athletelink.authservice.exception.IncorrectPasswordException;
import team.anonyms.athletelink.authservice.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final BCryptPasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

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

        userRepository.save(userToRegister);
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

        userToChangePassword.setPassword(changePasswordRequestServiceDto.newPassword());
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
}
