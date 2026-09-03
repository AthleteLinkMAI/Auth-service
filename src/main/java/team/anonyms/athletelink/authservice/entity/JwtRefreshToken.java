package team.anonyms.athletelink.authservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "jwt_refresh_tokens")
public class JwtRefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID deviceId;

    @NotBlank
    private String token;

    @NotNull
    private Instant expiration;

    @NotNull
    private Instant lastUsed;

    @NotBlank
    private String userAgent;

    @NotBlank
    private String ipAddress;

    @NotNull
    @ManyToOne(optional = false)
    private User user;
}
