package today.seasoning.seasoning.common.token.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.common.token.domain.TokenInfo;
import today.seasoning.seasoning.common.util.JwtUtil;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {

    // 리프레시 토큰을 통한 액세스 토큰 재발급
    public TokenInfo refresh(String refreshToken) {
        if (!JwtUtil.validate(refreshToken)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Invalid Token");
        }
        return JwtUtil.refreshToken(refreshToken);
    }
}
