package today.seasoning.seasoning.user.service.kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.common.token.domain.TokenInfo;
import today.seasoning.seasoning.common.util.JwtUtil;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;
import today.seasoning.seasoning.user.dto.LoginResult;
import today.seasoning.seasoning.user.dto.SocialUserProfileDto;

import java.util.Optional;
import today.seasoning.seasoning.user.event.SignedUpEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    private final ExchangeKakaoAccessToken exchangeKakaoAccessToken;
    private final FetchKakaoUserProfile fetchKakaoUserProfile;
    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;

    @Transactional
    public LoginResult handleKakaoLogin(String authorizationCode) {
        // 액세스 토큰 교환
        String accessToken = exchangeKakaoAccessToken.doExchange(authorizationCode);

        // 카카오 프로필 조회
        SocialUserProfileDto userProfile = fetchKakaoUserProfile.doFetch(accessToken);

        // 로그인 처리 (신규 유저인 경우 회원 등록)
        LoginInfo loginInfo = handleLogin(userProfile);

        // 토큰 발급
        TokenInfo tokenInfo = JwtUtil.createToken(loginInfo.getUser().getId());

        return new LoginResult(tokenInfo, loginInfo.isFirstLogin());
    }

    private LoginInfo handleLogin(SocialUserProfileDto userProfile) {
        Optional<User> foundUser = userRepository.find(userProfile.getEmail(), LoginType.KAKAO);

        if (foundUser.isEmpty()) {
            User user = userRepository.save(userProfile.toEntity(LoginType.KAKAO));
            eventPublisher.publishEvent(new SignedUpEvent(user));
            return new LoginInfo(user, true);
        }
        return new LoginInfo(foundUser.get(), false);
    }

    @Getter
    @AllArgsConstructor
    private static class LoginInfo {

        private User user;
        private boolean firstLogin;
    }
}
