package team.anonyms.athletelink.authservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import team.anonyms.athletelink.authservice.dto.controller.session.SessionControllerDto;

import java.time.Instant;
import java.util.UUID;

/**
 * {@code JwtRefreshToken} contains refresh token data and {@link SessionControllerDto}.
 *
 * <p>
 *     It is necessary to keep one entity and don't split it
 *     since {@link SessionControllerDto} and {@code JwtRefreshToken} share common {@code expiration} field.
 * </p>
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "jwt_refresh_tokens")
public class JwtRefreshToken {
    @Id
    private UUID jti;

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
