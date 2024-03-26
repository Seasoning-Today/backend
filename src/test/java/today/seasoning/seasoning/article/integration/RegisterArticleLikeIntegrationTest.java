package today.seasoning.seasoning.article.integration;

import static today.seasoning.seasoning.notification.domain.NotificationType.ARTICLE_FEEDBACK;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import today.seasoning.seasoning.BaseIntegrationTest;
import today.seasoning.seasoning.article.domain.Article;
import today.seasoning.seasoning.article.domain.ArticleLike;
import today.seasoning.seasoning.article.domain.ArticleLikeRepository;
import today.seasoning.seasoning.article.domain.ArticleRepository;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.friendship.domain.Friendship;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;
import today.seasoning.seasoning.notification.domain.UserNotification;
import today.seasoning.seasoning.notification.domain.UserNotificationRepository;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@DisplayName("기록장 좋아요 등록 통합 테스트")
public class RegisterArticleLikeIntegrationTest extends BaseIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ArticleLikeRepository articleLikeRepository;

    @Autowired
    FriendshipRepository friendshipRepository;

    @Autowired
    UserNotificationRepository userNotificationRepository;

    @InjectSoftAssertions
    SoftAssertions softAssertions;

    private void makeFriendships(User user, User friend) {
        friendshipRepository.save(new Friendship(user, friend));
        friendshipRepository.save(new Friendship(friend, user));
    }

    @Test
    @DisplayName("성공 - 좋아요 신규 요청(자신의 공개 기록장)")
    void test() {
        //given
        User user = userRepository.save(new User("nickname", "https://test.org/user0.jpg", "user@email.com", LoginType.KAKAO));
        Article article = articleRepository.save(new Article(user, true, 2024, 1, "contents"));

        //when : 자신의 공개 기록장에 좋아요 요청 시
        String url = "/article/" + TsidUtil.toString(article.getId()) + "/like";
        ExtractableResponse<Response> response = post(url, user.getId());

        //then
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);

        softAssertions.assertThat(articleLikeRepository.findByArticleAndUser(article.getId(), user.getId()))
            .as("해당 기록장에 대한 좋아요가 저장되어야 한다")
            .isPresent();

        softAssertions.assertThat(userNotificationRepository.count())
            .as("자신의 기록장에 누른 좋아요는 알림이 발생하지 않아야 한다")
            .isEqualTo(0);
    }

    @Test
    @DisplayName("성공 - 좋아요 신규 요청(자신의 비공개 기록장)")
    void test5() {
        //given
        User user = userRepository.save(new User("nickname", "https://test.org/user0.jpg", "user@email.com", LoginType.KAKAO));
        Article article = articleRepository.save(new Article(user, true, 2024, 1, "contents"));

        //when : 자신의 비공개 기록장에 좋아요 요청 시
        String url = "/article/" + TsidUtil.toString(article.getId()) + "/like";
        ExtractableResponse<Response> response = post(url, user.getId());

        //then
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);

        softAssertions.assertThat(articleLikeRepository.findByArticleAndUser(article.getId(), user.getId()))
            .as("해당 기록장에 대한 회원의 좋아요가 저장되어야 한다")
            .isPresent();

        softAssertions.assertThat(userNotificationRepository.count())
            .as("자신의 기록장에 누른 좋아요는 알림이 발생하지 않아야 한다")
            .isEqualTo(0);
    }

    @Test
    @DisplayName("성공 - 좋아요 신규 요청(친구의 공개 기록장)")
    void test2() {
        //given
        User user = userRepository.save(new User("nickname1", "https://test.org/user0.jpg", "user1@email.com", LoginType.KAKAO));
        User friend = userRepository.save(new User("nickname2", "https://test.org/user1.jpg", "user2@email.com", LoginType.KAKAO));
        Article friendArticle = articleRepository.save(new Article(friend, true, 2024, 1, "contents"));
        makeFriendships(user, friend);

        //when : 친구의 공개 기록장에 좋아요 요청 시
        String url = "/article/" + TsidUtil.toString(friendArticle.getId()) + "/like";
        ExtractableResponse<Response> response = post(url, user.getId());

        //then 1
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);

        softAssertions.assertThat(articleLikeRepository.findByArticleAndUser(friendArticle.getId(), user.getId()))
            .as("해당 기록장에 대한 회원의 좋아요가 저장되어야 한다")
            .isPresent();

        //then 2 : 저장된 기록장 좋아요 알림 확인
        softAssertions.assertThat(userNotificationRepository.count()).isEqualTo(1);

        List<UserNotification> friendNotifications = userNotificationRepository.findByReceiverId(friend.getId());
        softAssertions.assertThat(friendNotifications.size()).isEqualTo(1);

        UserNotification notification = friendNotifications.get(0);
        softAssertions.assertThat(notification.getType()).as("알림 유형: 기록장 좋아요").isEqualTo(ARTICLE_FEEDBACK);
        softAssertions.assertThat(notification.getSenderId()).as("송신자: 회원").isEqualTo(user.getId());
        softAssertions.assertThat(notification.getReceiverId()).as("수신자: 친구").isEqualTo(friend.getId());
        softAssertions.assertThat(notification.isRead()).as("상태: 읽지 않음").isFalse();
        softAssertions.assertThat(notification.getMessage()).as("메시지: 친구의 기록장 아이디").isEqualTo(TsidUtil.toString(friendArticle.getId()));
    }

    @Test
    @DisplayName("실패 - 좋아요 신규 요청(친구의 비공개 기록장)")
    void test4() {
        //given
        User user = userRepository.save(new User("nickname1", "https://test.org/user0.jpg", "user1@email.com", LoginType.KAKAO));
        User friend = userRepository.save(new User("nickname2", "https://test.org/user1.jpg", "user2@email.com", LoginType.KAKAO));
        Article friendArticle = articleRepository.save(new Article(friend, false, 2024, 1, "contents"));
        makeFriendships(user, friend);

        //when : 친구의 비공개 기록장에 좋아요 요청 시
        String url = "/article/" + TsidUtil.toString(friendArticle.getId()) + "/like";
        ExtractableResponse<Response> response = post(url, user.getId());

        //then : 해당 요청은 거부되어야 한다
        softAssertions.assertThat(response.statusCode()).isNotEqualTo(200);
        softAssertions.assertThat(articleLikeRepository.count()).isEqualTo(0);
        softAssertions.assertThat(userNotificationRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("실패 - 좋아요 신규 요청(타인의 기록장)")
    void test3() {
        //given
        User user = userRepository.save(new User("nickname1", "https://test.org/user0.jpg", "user1@email.com", LoginType.KAKAO));
        User stranger = userRepository.save(new User("nickname2", "https://test.org/user1.jpg", "user2@email.com", LoginType.KAKAO));
        Article strangerArticle = articleRepository.save(new Article(stranger, true, 2024, 1, "contents"));

        //when : 친구가 아닌 회원의 기록장에 좋아요 요청 시
        String url = "/article/" + TsidUtil.toString(strangerArticle.getId()) + "/like";
        ExtractableResponse<Response> response = post(url, user.getId());

        //then : 해당 요청은 거부되어야 한다
        softAssertions.assertThat(response.statusCode()).isNotEqualTo(200);
        softAssertions.assertThat(articleLikeRepository.count()).isEqualTo(0);
        softAssertions.assertThat(userNotificationRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("성공 - 좋아요 중복 요청")
    void test6() {
        //given : 회원이 친구의 공개 기록장에 좋아요를 누른 상태에서
        User user = userRepository.save(new User("nickname", "https://test.org/user0.jpg", "user@email.com", LoginType.KAKAO));
        User friend = userRepository.save(new User("nickname2", "https://test.org/user1.jpg", "user2@email.com", LoginType.KAKAO));
        Article friendArticle = articleRepository.save(new Article(friend, true, 2024, 1, "contents"));
        ArticleLike articleLike = articleLikeRepository.save(new ArticleLike(friendArticle, user));
        makeFriendships(user, friend);

        //when : 좋아요를 중복으로 요청 시
        String url = "/article/" + TsidUtil.toString(friendArticle.getId()) + "/like";
        ExtractableResponse<Response> response = post(url, user.getId());

        //then : 요청은 성공하지만, 실질적인 상태 변화는 발생하지 않는다
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        softAssertions.assertThat(articleLikeRepository.count()).isEqualTo(1);
        softAssertions.assertThat(articleLikeRepository.findById(articleLike.getId())).isPresent();
        softAssertions.assertThat(userNotificationRepository.count()).isEqualTo(0);
    }
}
