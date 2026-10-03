package team.anonyms.athletelink.authservice.entity.code;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "email_verification_codes")
public class EmailCode extends VerificationCode {
}
