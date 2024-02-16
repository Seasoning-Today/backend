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
import today.seasoning.seasoning.common.aws.UploadFileInfo;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.notification.domain.NotificationType;
import today.seasoning.seasoning.notification.domain.UserNotification;
import today.seasoning.seasoning.notification.domain.UserNotificationRepository;
import today.seasoning.seasoning.notification.dto.UserNotificationResponse;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;
import today.seasoning.seasoning.user.dto.UserProfileResponse;

@DisplayName("알림 조회 통합테스트")
public class FindNotificationsIntegrationTest extends BaseIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserNotificationRepository userNotificationRepository;

    @InjectSoftAssertions
    SoftAssertions softAssertions;

    static final String url = "/notification";

    List<User> users;

    @BeforeEach
    void initMockUsers() {
        users = List.of(
            new User("userNickname0", "https://test.org/user0.jpg", "user0@email.com", LoginType.KAKAO),
            new User("userNickname1", "https://test.org/user1.jpg", "user1@email.com", LoginType.KAKAO)
        );
        userRepository.saveAll(users);
    }

    @Test
    @DisplayName("성공")
    void test() throws Exception {
        //given : sender로 부터 receiver에게 친구 요청 알림이 전송된 경우
        User sender = users.get(0);
        User receiver = users.get(1);

        UserNotification notification = userNotificationRepository.save(UserNotification.builder()
            .type(NotificationType.FRIENDSHIP_REQUEST)
            .senderId(sender.getId())
            .receiverId(receiver.getId())
            .build());

        //when : receiver가 알림을 조회했을 때
        ExtractableResponse<Response> response = get(url, receiver.getId());

        //then
        softAssertions.assertThat(response.statusCode())
            .as("상태 코드는 200이어야 한다")
            .isEqualTo(200);

        List<UserNotificationResponse> notificationResponses = response.body().as(new TypeRef<>() {
        });

        softAssertions.assertThat(notificationResponses.size())
            .as("조회된 알림의 개수는 1개이어야 한다")
            .isEqualTo(1);

        UserNotificationResponse expectedNotificationResponse = UserNotificationResponse.builder()
            .id(TsidUtil.toString(notification.getId()))
            .type(notification.getType().name())
            .created(notification.getCreatedDate())
            .profile(UserProfileResponse.build(sender))
            .message(null)
            .read(false)
            .build();

        softAssertions.assertThat(notificationResponses.get(0))
            .as("조회된 알림의 내용은 expectedNotificationResponse와 동일해야 한다")
            .usingRecursiveComparison()
            .isEqualTo(expectedNotificationResponse);
    }

    @Test
    @DisplayName("성공 - 상대방의 프로필이 변경된 경우, 새로운 프로필이 조회되어야 한다")
    void test2() throws Exception {
        //given : sender로부터 receiver에게 친구 요청 알림이 전송된 이후, sender의 프로필이 변경된 경우
        User sender = users.get(0);
        User receiver = users.get(1);

        userNotificationRepository.save(UserNotification.builder()
            .type(NotificationType.FRIENDSHIP_REQUEST)
            .senderId(sender.getId())
            .receiverId(receiver.getId())
            .build());

        String newSenderNickname = "newNickname";
        String newSenderAccountId = "new_account_id";
        UploadFileInfo newSenderImage = new UploadFileInfo("new.jpg", "https://test.org/new.jpg");

        sender.updateProfile(newSenderNickname, newSenderAccountId);
        sender.changeProfileImage(newSenderImage);
        userRepository.save(sender);

        //when : receiver가 알림을 조회했을 때
        ExtractableResponse<Response> response = get(url, receiver.getId());

        //then
        List<UserNotificationResponse> notificationResponses = response.body().as(new TypeRef<>() {
        });

        UserProfileResponse actualProfile = notificationResponses.get(0).getProfile();

        UserProfileResponse newProfile = new UserProfileResponse(sender.getId(),
            newSenderNickname,
            newSenderAccountId,
            newSenderImage.getUrl());

        softAssertions.assertThat(actualProfile)
            .as("조회된 프로필 정보는 새로운 프로필과 동일해야 한다")
            .usingRecursiveComparison()
            .isEqualTo(newProfile);
    }
}
