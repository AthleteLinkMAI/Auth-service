package team.anonyms.athletelink.authservice.entity.code;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import team.anonyms.athletelink.authservice.entity.UserCredentials;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class VerificationCode {
    @Id
    private UUID userId;

    @Size(max = 6)
    @NotBlank
    private String code;

    @NotNull
    private Instant expiration;

    @MapsId
    @JoinColumn(name = "user_id")
    @OneToOne(optional = false)
    private UserCredentials userCredentials;
}
