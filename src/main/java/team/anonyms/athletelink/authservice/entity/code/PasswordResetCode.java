package team.anonyms.athletelink.authservice.entity.code;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "password_reset_codes")
public class PasswordResetCode extends VerificationCode {
}
