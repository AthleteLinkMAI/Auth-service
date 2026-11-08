package team.anonyms.athletelink.authservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import team.anonyms.athletelink.authservice.dto.service.kafka.UserToCreate;
import team.anonyms.athletelink.authservice.dto.service.kafka.UserToDelete;
import team.anonyms.athletelink.authservice.dto.service.kafka.UserToUpdate;
import team.anonyms.athletelink.authservice.dto.service.user.*;
import team.anonyms.athletelink.authservice.entity.UserCredentials;
import team.anonyms.athletelink.authservice.exception.AccessForbiddenException;
import team.anonyms.athletelink.authservice.exception.EntityAlreadyExistsException;
import team.anonyms.athletelink.authservice.exception.IncorrectPasswordException;
import team.anonyms.athletelink.authservice.repository.UserCredentialsRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserCredentialsService {
    private final KafkaProducerService kafkaProducerService;
    private final BanService banService;
    private final UserCredentialsRepository userCredentialsRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Registers new user in the system.
     *
     * @param dto credentials to register.
     * @throws IllegalArgumentException if specified email already exists in DB.
     */
    public void register(RegisterRequestServiceDto dto) {
        String email = dto.email();

        if (userCredentialsRepository.findByEmail(email).isPresent()) {
            throw new EntityAlreadyExistsException("User with this email already exists; email=" + email);
        }

        String nickname = dto.nickname();

        if (userCredentialsRepository.findByNickname(nickname).isPresent()) {
            throw new EntityAlreadyExistsException("User with this nickname already exists; nickname=" + nickname);
        }

        UserCredentials userCredentialsToRegister = UserCredentials.builder()
                .role(UserCredentials.UserRoleType.USER)
                .email(email)
                .password(passwordEncoder.encode(dto.password()))
                .isVerified(false)
                .build();

        UserCredentials userCredentialsRegistered = userCredentialsRepository.save(userCredentialsToRegister);
        kafkaProducerService.sendUserToCreate(
                new UserToCreate(
                        userCredentialsRegistered.getUserId(),
                        userCredentialsRegistered.getRole(),
                        dto.nickname(),
                        dto.name(),
                        dto.surname(),
                        dto.birthdate(),
                        dto.email(),
                        false,
                        false
                )
        );
    }

    public void changePassword(ChangePasswordRequestServiceDto dto) {
        UserCredentials userCredentialsToChangePassword = userCredentialsRepository.findById(dto.userId()).orElseThrow(
                        () -> new EntityNotFoundException("User not found; id=" + dto.userId())
        );

        if (!passwordEncoder.matches(dto.oldPassword(), userCredentialsToChangePassword.getPassword())) {
            throw new IncorrectPasswordException("Passwords don't match");
        }

        userCredentialsToChangePassword.setPassword(passwordEncoder.encode(dto.newPassword()));
        userCredentialsRepository.save(userCredentialsToChangePassword);
    }

    public void changeEmail(UUID userId, String newEmail) {
        UserCredentials userCredentialsToChangeEmail = userCredentialsRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found; id=" + userId)
        );

        userCredentialsToChangeEmail.setEmail(newEmail);
        userCredentialsToChangeEmail.setIsVerified(Boolean.FALSE);
        userCredentialsRepository.save(userCredentialsToChangeEmail);
        kafkaProducerService.sendUserToUpdate(
                new UserToUpdate(
                        userCredentialsToChangeEmail.getUserId(),
                        userCredentialsToChangeEmail.getRole(),
                        userCredentialsToChangeEmail.getEmail(),
                        userCredentialsToChangeEmail.getIsVerified(),
                        false
                )
        );
    }

    public UserCredentialsServiceDto updateUserRole(UpdateRoleRequestServiceDto dto) {
        UserCredentials userToUpdate = userCredentialsRepository.findById(dto.userId()).orElseThrow(
                () -> new EntityNotFoundException("User is not found; id=" + dto.userId())
        );

        if (dto.newRole().equals(UserCredentials.UserRoleType.ADMIN)) {
            throw new AccessForbiddenException("It is impossible to promote a user to an admin");
        }

        userToUpdate.setRole(dto.newRole());
        userCredentialsRepository.save(userToUpdate);

        kafkaProducerService.sendUserToUpdate(
                new UserToUpdate(
                        userToUpdate.getUserId(),
                        userToUpdate.getRole(),
                        userToUpdate.getEmail(),
                        userToUpdate.getIsVerified(),
                        banService.isUserBanned(userToUpdate)
                )
        );

        return new UserCredentialsServiceDto(
                userToUpdate.getUserId(),
                userToUpdate.getRole(),
                userToUpdate.getEmail(),
                userToUpdate.getIsVerified(),
                banService.isUserBanned(userToUpdate)
        );
    }

    public void deleteUser(UUID userId) {
        if (!userCredentialsRepository.existsById(userId)) {
            return;
        }

        userCredentialsRepository.deleteById(userId);
        kafkaProducerService.sendUserToDelete(new UserToDelete(userId));
    }
}
