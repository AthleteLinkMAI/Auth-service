package team.anonyms.athletelink.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import team.anonyms.athletelink.authservice.entity.UserCredentials;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RedisService {
    public static final String DENYLIST_PREFIX = "jwt:denylist:";
    public static final String BANLIST_PREFIX = "user:banlist:";

    private final StringRedisTemplate stringRedisTemplate;

    public void addJwtAccessTokenToDenylist(String jwtAccessToken) {
        stringRedisTemplate.opsForValue().set(
                DENYLIST_PREFIX + jwtAccessToken,
                "",
                JwtService.ACCESS_TOKEN_DURATION
        );
    }

    public void addUserToBanlist(UserCredentials userCredentials) {
        addUserToBanlist(userCredentials.getUserId());
    }

    public void addUserToBanlist(UUID userId) {
        stringRedisTemplate.opsForValue().set(
                BANLIST_PREFIX + userId,
                "",
                JwtService.ACCESS_TOKEN_DURATION
        );
    }

    public void removeUserFromBanlist(UserCredentials userCredentials) {
        removeUserFromBanlist(userCredentials.getUserId());
    }

    public void removeUserFromBanlist(UUID userId) {
        stringRedisTemplate.delete(BANLIST_PREFIX + userId);
    }
}
