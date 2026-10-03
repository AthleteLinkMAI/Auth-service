package team.anonyms.athletelink.authservice.entity.code;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import team.anonyms.athletelink.authservice.entity.User;

import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 6)
    @NotBlank
    private String code;

    @NotNull
    private Instant expiration;

    @NotNull
    @OneToOne(optional = false)
    private User user;
}
