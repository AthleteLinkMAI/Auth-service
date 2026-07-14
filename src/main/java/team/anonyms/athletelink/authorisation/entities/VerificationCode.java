package team.anonyms.athletelink.authorisation.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import team.anonyms.athletelink.authorisation.enums.VerificationCodeType;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "verification_codes")
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 6)
    @NotBlank
    private String code;

    @NotNull
    private VerificationCodeType type;

    @NotNull
    private Instant expiration;

    @NotNull
    @OneToOne(optional = false)
    private User user;
}
