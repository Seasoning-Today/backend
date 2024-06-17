package today.seasoning.seasoning.user.event;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.support.TransactionTemplate;
import today.seasoning.seasoning.BaseIntegrationTest;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@DisplayName("회원가입 이벤트 핸들러 통합 테스트")
@Sql(scripts = "classpath:data/insert_official_account_user.sql")
class SignedUpEventHandlerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Autowired
    FriendshipRepository friendshipRepository;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @InjectSoftAssertions
    SoftAssertions softAssertions;

    @Value("${OFFICIAL_ACCOUNT_USER_ID}")
    private Long officialAccountUserId;

    private User user;

    @BeforeEach
    void registerUser() {
        user = userRepository.save(new User("nickname0", "https://test.org/user0.jpg", "user0@email.com", LoginType.KAKAO));
    }

    @Test
    @DisplayName("회원가입 이벤트 발생 시, 공식 계정을 신규 회원의 친구로 등록한다")
    void test1() {
        //given
        SignedUpEvent signedUpEvent = new SignedUpEvent(user);

        //when
        transactionTemplate.executeWithoutResult(status -> applicationEventPublisher.publishEvent(signedUpEvent));

        //then
        softAssertions.assertThat(friendshipRepository.count()).isEqualTo(2);
        softAssertions.assertThat(friendshipRepository.existsByUserIdAndFriendId(officialAccountUserId, user.getId())).isTrue();
        softAssertions.assertThat(friendshipRepository.existsByUserIdAndFriendId(user.getId(), officialAccountUserId)).isTrue();
    }

    @Test
    @DisplayName("회원가입이 실패한 경우, 발행된 회원가입 이벤트는 처리되지 않는다")
    void test2() {
        //given
        SignedUpEvent signedUpEvent = new SignedUpEvent(user);

        //when
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            applicationEventPublisher.publishEvent(signedUpEvent);
            transactionStatus.setRollbackOnly();
        });

        //then
        softAssertions.assertThat(friendshipRepository.count()).isEqualTo(0);
    }
}
