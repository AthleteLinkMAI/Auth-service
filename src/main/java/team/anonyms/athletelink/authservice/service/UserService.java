package team.anonyms.athletelink.authservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.anonyms.athletelink.authservice.dto.service.kafka.UserChange;
import team.anonyms.athletelink.authservice.dto.service.kafka.enums.UserChangeType;
import team.anonyms.athletelink.authservice.dto.service.user.*;
import team.anonyms.athletelink.authservice.entity.User;
import team.anonyms.athletelink.authservice.exception.EntityAlreadyExistsException;
import team.anonyms.athletelink.authservice.exception.IncorrectPasswordException;
import team.anonyms.athletelink.authservice.mapper.UserMapper;
import team.anonyms.athletelink.authservice.repository.UserRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final KafkaProducerService kafkaProducerService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder;

    public static Integer countUserAge(User user) {
        return Period.between(user.getBirthdate(), LocalDate.now()).getYears();
    }

    public UserServiceDto me(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User was not found; id=" + userId)
        );

        return new UserServiceDto(
                userId,
                user.getNickname(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getIsVerified(),
                countUserAge(user),
                user.getRegistrationDate()
        );
    }

    public List<UserPublicServiceDto> getUsersByUserIds(List<UUID> userIds) {
        List<UserPublicServiceDto> usersToReturn = new LinkedList<>();

        List<User> usersFound = userRepository.findAllById(userIds);
        for (User user : usersFound) {
            usersToReturn.add(userMapper.userPublicServiceDtoFromUser(user));
        }

        return usersToReturn;
    }

    /**
     * Registers new user in the system.
     *
     * @param dto credentials to register.
     * @throws IllegalArgumentException if specified email already exists in DB.
     */
    @Transactional
    public void register(RegisterRequestServiceDto dto) {
        String email = dto.email();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new EntityAlreadyExistsException("User with this email already exists; email=" + email);
        }

        String nickname = dto.nickname();

        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new EntityAlreadyExistsException("User with this nickname already exists; nickname=" + nickname);
        }

        User userToRegister = User.builder()
                .nickname(nickname)
                .name(dto.name())
                .surname(dto.surname())
                .email(email)
                .password(passwordEncoder.encode(dto.password()))
                .isVerified(false)
                .birthdate(dto.birthdate())
                .build();

        User userRegistered = userRepository.save(userToRegister);
        kafkaProducerService.sendUserChange(
                new UserChange(UserChangeType.CREATE, userRegistered.getId())
        );
    }

    @Transactional
    public void updateUser(UserUpdateServiceDto dto) {
        User userToUpdate = userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found; id=" + dto.userId()));

        userToUpdate.setNickname(dto.nickname());
        userToUpdate.setName(dto.name());
        userToUpdate.setSurname(dto.surname());
        userToUpdate.setBirthdate(dto.birthdate());

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
