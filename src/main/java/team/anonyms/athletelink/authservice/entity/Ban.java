package team.anonyms.athletelink.authservice.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class Ban {
    @Id
    private UUID userId;

    @Nullable
    private Instant expiration;

    @Nullable
    private String reason;

    @NotNull
    private UUID bannedBy;

    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "userId")
    private UserCredentials userCredentials;
}
