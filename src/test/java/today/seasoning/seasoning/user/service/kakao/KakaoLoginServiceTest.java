package today.seasoning.seasoning.user.service.kakao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mockStatic;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.never;
import static today.seasoning.seasoning.common.enums.LoginType.KAKAO;

import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import today.seasoning.seasoning.common.util.JwtUtil;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;
import today.seasoning.seasoning.user.dto.LoginResult;
import today.seasoning.seasoning.user.dto.SocialUserProfileDto;
import today.seasoning.seasoning.user.event.SignedUpEvent;

@DisplayName("카카오 로그인 단위 테스트")
@ExtendWith(MockitoExtension.class)
class KakaoLoginServiceTest {

    @Mock
    private ExchangeKakaoAccessToken exchangeKakaoAccessToken;
    @Mock
    private FetchKakaoUserProfile fetchKakaoUserProfile;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    KakaoLoginService kakaoLoginService;

    private static MockedStatic<JwtUtil> jwtUtil;

    @BeforeAll
    static void beforeAll() {
        jwtUtil = mockStatic(JwtUtil.class);
    }

    @AfterAll
    static void afterAll() {
        jwtUtil.close();
    }

    @Test
    @DisplayName("비회원 로그인")
    void test() {
        //given
        SocialUserProfileDto newUserProfile = new SocialUserProfileDto("user", "user@test.org", "https://test.org/image.jpg");
        User newUser = newUserProfile.toEntity(KAKAO);

        given(fetchKakaoUserProfile.doFetch(any())).willReturn(newUserProfile);
        // 가입된 회원이 아닌 경우
        given(userRepository.find(anyString(), any())).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willReturn(newUser);

        //when: 카카오 로그인 시
        LoginResult loginResult = kakaoLoginService.handleKakaoLogin("pseudo-authorization-code");

        //then: 사용자를 신규 회원으로 등록해야 한다
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        //then 1: 등록된 회원의 정보는 프로필 정보와 일치해야 한다
        assertThat(savedUser.getLoginType()).isEqualTo(KAKAO);
        assertThat(savedUser.getEmail()).isEqualTo(newUserProfile.getEmail());
        assertThat(savedUser.getNickname()).isEqualTo(newUserProfile.getNickname());
        assertThat(savedUser.getProfileImageUrl()).isEqualTo(newUserProfile.getProfileImageUrl());

        //then 2: 등록된 회원에 대한 회원가입 이벤트가 발행되어야 한다
        ArgumentCaptor<SignedUpEvent> publishedEventCaptor = ArgumentCaptor.forClass(SignedUpEvent.class);
        verify(eventPublisher).publishEvent(publishedEventCaptor.capture());
        SignedUpEvent publishedEvent = publishedEventCaptor.getValue();

        assertThat(publishedEvent.getSignedUpUser()).isEqualTo(newUser);

        //then 3: LoginResult.firstLogin의 값은 true이어야 한다
        assertThat(loginResult.isFirstLogin()).isTrue();
    }

    @Test
    @DisplayName("회원 로그인")
    void test2() {
        //given
        SocialUserProfileDto userProfile = new SocialUserProfileDto("user", "user@test.org", "https://test.org/image.jpg");
        User user = userProfile.toEntity(KAKAO);

        given(fetchKakaoUserProfile.doFetch(any())).willReturn(userProfile);
        // 가입된 회원의 경우
        given(userRepository.find(anyString(), any())).willReturn(Optional.of(user));

        //when: 카카오 로그인 시
        LoginResult loginResult = kakaoLoginService.handleKakaoLogin("pseudo-authorization-code");

        //then 1: 등록된 회원에 대한 토큰을 발행해야 한다
        jwtUtil.verify(() -> JwtUtil.createToken(user.getId()));

        //then 2: 회원가입 이벤트는 발행되지 않아야 한다
        verify(eventPublisher, never()).publishEvent(any(SignedUpEvent.class));

        //then 3: LoginResult.firstLogin의 값은 false이어야 한다
        assertThat(loginResult.isFirstLogin()).isFalse();
    }

}
