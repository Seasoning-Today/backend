package today.seasoning.seasoning.notification.integration;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import today.seasoning.seasoning.BaseIntegrationTest;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.notification.domain.NotificationType;
import today.seasoning.seasoning.notification.domain.UserNotification;
import today.seasoning.seasoning.notification.domain.UserNotificationRepository;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@DisplayName("새로운 알림 조회 통합 테스트")
public class CheckUnreadNotificationsExistIntegrationTest extends BaseIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserNotificationRepository userNotificationRepository;

    @InjectSoftAssertions
    SoftAssertions softAssertions;

    static final String url = "/notification/new";

    List<User> users;

    @BeforeEach
    void initMockUsers() {
        users = userRepository.saveAll(List.of(
            new User("userNickname0", "https://test.org/user0.jpg", "user0@email.com", LoginType.KAKAO),
            new User("userNickname1", "https://test.org/user1.jpg", "user1@email.com", LoginType.KAKAO)
        ));
    }

    @Test
    @DisplayName("읽지 않은 알림이 있다면 true를 응답한다")
    void test1() throws Exception {
        //given : 친구 수락 알림은 읽었지만, 기록장 좋아요 알림은 읽지 않은 경우
        User user = users.get(0);
        User friend = users.get(1);

        registerReadNotification(friend, user, NotificationType.FRIENDSHIP_ACCEPTED);
        registerUnreadNotification(friend, user, NotificationType.ARTICLE_FEEDBACK);

        //when : 새로운 알림 조회를 요청했을 때
        ExtractableResponse<Response> response = get(url, user.getId());

        boolean result = response.body().as(new TypeRef<>() {
        });

        //then
        softAssertions.assertThat(response.statusCode())
            .as("상태 코드는 200이어야 한다")
            .isEqualTo(200);

        softAssertions.assertThat(result)
            .as("조회 결과는 true이어야 한다")
            .isTrue();
    }

    @Test
    @DisplayName("알림이 없으면 false를 응답한다")
    void test2() throws Exception {
        //given : 회원에게 전송된 알림이 없다면
        User user = users.get(0);

        //when : 새로운 알림 조회를 요청했을 때
        ExtractableResponse<Response> response = get(url, user.getId());
        boolean result = response.body().as(new TypeRef<>() {
        });

        //then
        softAssertions.assertThat(response.statusCode())
            .as("상태 코드는 200이어야 한다")
            .isEqualTo(200);

        softAssertions.assertThat(result)
            .as("조회 결과는 false이어야 한다")
            .isFalse();
    }

    @Test
    @DisplayName("모든 알림을 읽은 경우 false를 응답한다")
    void test3() throws Exception {
        //given : 회원이 모든 알림을 읽었다면
        User user = users.get(0);
        User friend = users.get(1);

        registerReadNotification(friend, user, NotificationType.FRIENDSHIP_ACCEPTED);
        registerReadNotification(friend, user, NotificationType.ARTICLE_FEEDBACK);

        //when :
        ExtractableResponse<Response> response = get(url, user.getId());

        //then
        softAssertions.assertThat(response.statusCode())
            .as("상태 코드는 200이어야 한다")
            .isEqualTo(200);

        boolean result = response.body().as(new TypeRef<>() {
        });
        softAssertions.assertThat(result)
            .as("조회 결과는 false이어야 한다")
            .isFalse();
    }

    private void registerReadNotification(User sender, User receiver, NotificationType type) {
        userNotificationRepository.save(UserNotification.builder()
            .type(type)
            .senderId(sender.getId())
            .receiverId(receiver.getId())
            .read(true)
            .build());
    }

    private void registerUnreadNotification(User sender, User receiver, NotificationType type) {
        userNotificationRepository.save(UserNotification.builder()
            .type(type)
            .senderId(sender.getId())
            .receiverId(receiver.getId())
            .read(false)
            .build());
    }
}
