package today.seasoning.seasoning.common.token.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.common.token.domain.TokenProperties;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.common.token.domain.TokenInfo;
import today.seasoning.seasoning.common.util.JwtUtil;
import today.seasoning.seasoning.common.util.TsidUtil;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {

    private final TokenProperties tokenProperties;
    private final RedisTemplate<String, String> redisTemplate;

    // 리프레시 토큰 저장
    public void save(String refreshToken, long userId) {
        Long expirationTimeMillis = tokenProperties.getRefreshTokenExpirationTimeMillis();

        redisTemplate.opsForValue()
            .set(refreshToken, TsidUtil.toString(userId), expirationTimeMillis, TimeUnit.MILLISECONDS);
    }

    // 리프레시 토큰을 통한 토큰 재발급
    public TokenInfo refresh(String oldRefreshToken) {
        // 토큰 유효성 검증
        validateToken(oldRefreshToken);

        // 토큰 보유 사용자 아이디 조회
        String userId = findUserId(oldRefreshToken);

        // 토큰 재발급
        TokenInfo tokenInfo = JwtUtil.refreshToken(TsidUtil.toLong(userId), oldRefreshToken);

        // 기존 리프레시 토큰 삭제 후 새로운 리프레시 토큰 저장
        redisTemplate.delete(oldRefreshToken);
        redisTemplate.opsForValue().set(tokenInfo.getRefreshToken(), userId);

        return tokenInfo;
    }

    private void validateToken(String refreshToken) {
        if (!JwtUtil.validate(refreshToken)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Invalid Token");
        }
    }

    private String findUserId(String refreshToken) {
        String userId = redisTemplate.opsForValue().get(refreshToken);
        if (userId == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Invalid Token");
        }
        return userId;
    }
}
