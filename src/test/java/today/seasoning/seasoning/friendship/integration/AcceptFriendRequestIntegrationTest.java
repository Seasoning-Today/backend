package today.seasoning.seasoning.friendship.integration;

import static today.seasoning.seasoning.notification.domain.NotificationType.FRIENDSHIP_REQUEST;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import today.seasoning.seasoning.BaseIntegrationTest;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.friendship.domain.FriendRequest;
import today.seasoning.seasoning.friendship.domain.FriendRequestRepository;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;
import today.seasoning.seasoning.notification.domain.NotificationType;
import today.seasoning.seasoning.notification.domain.UserNotification;
import today.seasoning.seasoning.notification.domain.UserNotificationRepository;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@DisplayName("친구 요청 수락 통합 테스트")
public class AcceptFriendRequestIntegrationTest extends BaseIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    FriendRequestRepository friendRequestRepository;

    @Autowired
    UserNotificationRepository userNotificationRepository;

    @Autowired
    FriendshipRepository friendshipRepository;

    @InjectSoftAssertions
    protected SoftAssertions softAssertions;

    static final String url = "/friend/add/accept";

    List<User> users;

    @BeforeEach
    void initMockUsers() {
        //given
        users = List.of(
            new User("userNickname0", "https://test.org/user0.jpg", "user0@email.com", LoginType.KAKAO),
            new User("userNickname1", "https://test.org/user1.jpg", "user1@email.com", LoginType.KAKAO)
        );
        userRepository.saveAll(users);
    }

    @Test
    @DisplayName("성공")
    void test1() throws Exception {
        //given
        User user = users.get(0);
        User requester = users.get(1);

        // 회원(user)에게 상대방(requester)이 친구 신청을 완료하고
        friendRequestRepository.save(new FriendRequest(requester, user));

        // 회원에게 친구 요청 알림이 전송된 경우
        userNotificationRepository.save(UserNotification.builder().type(FRIENDSHIP_REQUEST)
            .senderId(requester.getId()).receiverId(user.getId()).build());

        //when : 회원이 친구 요청을 수락했을 때
        JSONObject requestBody = new JSONObject().put("id", TsidUtil.toString(requester.getId()));
        ExtractableResponse<Response> response = post(url, user.getId(), requestBody);

        //then
        softAssertions.assertThat(response.statusCode())
            .as("상태 코드는 200이어야 한다")
            .isEqualTo(200);

        softAssertions.assertThat(friendshipRepository.existsByUserIdAndFriendId(user.getId(), requester.getId()))
            .as("회원의 친구 목록에 상대방이 추가되어야 한다")
            .isTrue();

        softAssertions.assertThat(friendshipRepository.existsByUserIdAndFriendId(requester.getId(), user.getId()))
            .as("상대방의 친구 목록에 회원이 추가되어야 한다")
            .isTrue();

        softAssertions.assertThat(friendRequestRepository.count())
            .as("수락된 친구 요청은 삭제되어야 한다")
            .isEqualTo(0);

        softAssertions.assertThat(userNotificationRepository.findByReceiverId(user.getId()).size())
            .as("수락된 친구 요청 알림은 삭제되어야 한다")
            .isEqualTo(0);

        List<UserNotification> requesterNotifications = userNotificationRepository.findByReceiverId(requester.getId());
        softAssertions.assertThat(requesterNotifications.size())
            .as("상대방에게 알림이 하나 전송되어야 한다")
            .isEqualTo(1);

        UserNotification notification = requesterNotifications.get(0);

        softAssertions.assertThat(notification.getSenderId())
            .as("알림의 전송자는 회원이어야 한다")
            .isEqualTo(user.getId());

        softAssertions.assertThat(notification.getType())
            .as("알림 유형은 친구 요청 수락이어야 한다")
            .isEqualTo(NotificationType.FRIENDSHIP_ACCEPTED);
    }

    @Test
    @DisplayName("실패 - 존재하지 않는 친구 요청 수락")
    void test2() throws Exception {
        //given
        User user = users.get(0);
        User someone = users.get(1);

        //when : 나에게 친구 신청하지 않은 상대방에 대해 친구 수락을 요청했을 때
        JSONObject requestBody = new JSONObject().put("id", TsidUtil.toString(someone.getId()));
        ExtractableResponse<Response> response = post(url, user.getId(), requestBody);

        //then
        softAssertions.assertThat(response.statusCode())
            .as("상태 코드는 400이어야 한다")
            .isEqualTo(400);
    }
}
