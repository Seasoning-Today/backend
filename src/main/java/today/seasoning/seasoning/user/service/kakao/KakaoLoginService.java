package today.seasoning.seasoning.user.service.kakao;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.common.util.JwtUtil;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;
import today.seasoning.seasoning.user.dto.LoginResultDto;
import today.seasoning.seasoning.user.dto.SocialUserProfileDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    private static final LoginType KAKAO_LOGIN_TYPE = LoginType.KAKAO;

    private final ExchangeKakaoAccessToken exchangeKakaoAccessToken;
    private final FetchKakaoUserProfile fetchKakaoUserProfile;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginResultDto handleKakaoLogin(String authorizationCode) {
        // 액세스 토큰 교환
        String accessToken = exchangeKakaoAccessToken.doExchange(authorizationCode);

        // 카카오 프로필 조회
        SocialUserProfileDto userProfile = fetchKakaoUserProfile.doFetch(accessToken);

        // 계정 조회 (신규 유저인 경우 가입 처리)
        UserInfo userInfo = registerUserIfNeeded(userProfile);

        // 토큰 발급
        String token = createToken(userInfo.getUser());

        return new LoginResultDto(token, userInfo.isFirstLogin());
    }

    private UserInfo registerUserIfNeeded(SocialUserProfileDto userProfile) {
        Optional<User> foundUser = userRepository.find(userProfile.getEmail(), KAKAO_LOGIN_TYPE);

        if (foundUser.isEmpty()) {
            User user = userRepository.save(userProfile.toEntity(LoginType.KAKAO));
            return new UserInfo(user, true);
        }
        return new UserInfo(foundUser.get(), false);
    }

    private String createToken(User user) {
        return jwtUtil.createToken(user.getId(), KAKAO_LOGIN_TYPE);
    }

    @Getter
    @AllArgsConstructor
    private static class UserInfo {

        private User user;
        private boolean firstLogin;
    }
}
