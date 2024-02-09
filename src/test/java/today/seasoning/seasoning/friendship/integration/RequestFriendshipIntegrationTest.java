package today.seasoning.seasoning.friendship.integration;

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
import today.seasoning.seasoning.friendship.domain.Friendship;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;
import today.seasoning.seasoning.notification.domain.UserNotificationRepository;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@DisplayName("친구 신청 통합 테스트")
public class RequestFriendshipIntegrationTest extends BaseIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    FriendshipRepository friendshipRepository;

    @Autowired
    FriendRequestRepository friendRequestRepository;

    @Autowired
    UserNotificationRepository userNotificationRepository;

    @InjectSoftAssertions
    SoftAssertions softAssertions;

    // API 주소
    static String uri = "/friend/add";

    // Mock 유저 정보
    User user;
    User friend;
    String friendId;

    @BeforeEach
    void initMocks() {
        user = new User("userNickname", "https://test.org/user.jpg", "user@email.com", LoginType.KAKAO);
        friend = new User("friendNickname", "https://test.org/friend.jpg", "friend@email.com", LoginType.KAKAO);
        friendId = TsidUtil.toString(friend.getId());
        userRepository.saveAll(List.of(user, friend));
    }

    @Test
    @DisplayName("성공")
    void success() throws Exception {
        //given
        JSONObject requestBody = new JSONObject()
            .put("id", friendId);

        //when
        ExtractableResponse<Response> response = post(uri, user.getId(), requestBody);

        //then
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        softAssertions.assertThat(checkFriendRequestExists(user, friend)).isTrue();
        softAssertions.assertThat(checkFriendRequestExists(friend, user)).isFalse();
        softAssertions.assertThat(userNotificationRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("실패 - 이미 친구인 경우, 409 응답을 한다")
    void failedByAlreadyFriends() throws Exception {
        //given
        friendshipRepository.save(new Friendship(user, friend));
        friendshipRepository.save(new Friendship(friend, user));

        JSONObject requestBody = new JSONObject()
            .put("id", friendId);

        //when
        ExtractableResponse<Response> response = post(uri, user.getId(), requestBody);

        //then
        softAssertions.assertThat(response.statusCode()).isEqualTo(409);
        softAssertions.assertThat(checkFriendRequestExists(user, friend)).isFalse();
        softAssertions.assertThat(userNotificationRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("실패 - 이미 신청한 경우, 409 응답을 한다")
    void failedByDuplicateRequest() throws Exception {
        //given
        friendRequestRepository.save(new FriendRequest(user, friend));

        JSONObject requestBody = new JSONObject()
            .put("id", friendId);

        //when
        ExtractableResponse<Response> response = post(uri, user.getId(), requestBody);

        //then
        softAssertions.assertThat(response.statusCode()).isEqualTo(409);
        softAssertions.assertThat(userNotificationRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("실패 - 자신에게 신청한 경우, 400 응답을 한다")
    void failedBySelfRequest() throws Exception {
        //given
        JSONObject requestBody = new JSONObject()
            .put("id", TsidUtil.toString(user.getId()));

        //when
        ExtractableResponse<Response> response = post(uri, user.getId(), requestBody);

        //then
        softAssertions.assertThat(response.statusCode()).isEqualTo(400);
        softAssertions.assertThat(checkFriendRequestExists(user, user)).isFalse();
        softAssertions.assertThat(userNotificationRepository.count()).isEqualTo(0);
    }

    private boolean checkFriendRequestExists(User fromUser, User toUser) {
        return friendRequestRepository.existsByFromUserIdAndToUserId(fromUser.getId(), toUser.getId());
    }

}
