package today.seasoning.seasoning.article.integration;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
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
import today.seasoning.seasoning.notification.domain.UserNotificationRepository;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@DisplayName("기록장 좋아요 취소 통합 테스트")
public class CancelArticleLikeIntegrationTest extends BaseIntegrationTest {

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

    private void cancelFriendships(User user, User friend) {
        friendshipRepository.findByUserIdAndFriendId(user.getId(), friend.getId()).ifPresent(friendshipRepository::delete);
        friendshipRepository.findByUserIdAndFriendId(friend.getId(), user.getId()).ifPresent(friendshipRepository::delete);
    }

    @Test
    @DisplayName("성공 - 자신의 공개 기록장")
    void test() {
        //given
        User user = userRepository.save(new User("nickname0", "https://test.org/user0.jpg", "user0@email.com", LoginType.KAKAO));
        Article article = articleRepository.save(new Article(user, true, 2024, 1, "contents"));
        ArticleLike articleLike = articleLikeRepository.save(new ArticleLike(article, user));

        //when : 자신의 공개 기록장에 좋아요 취소 요청 시
        String url = "/article/" + TsidUtil.toString(article.getId()) + "/like";
        ExtractableResponse<Response> response = delete(url, user.getId(), null);

        //then : 좋아요는 성공적으로 삭제되어야 한다
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        softAssertions.assertThat(articleLikeRepository.count()).isEqualTo(0);
        softAssertions.assertThat(articleLikeRepository.findById(articleLike.getId())).isEmpty();
    }

    @Test
    @DisplayName("성공 - 자신의 비공개 기록장")
    void test2() {
        //given
        User user = userRepository.save(new User("nickname0", "https://test.org/user0.jpg", "user0@email.com", LoginType.KAKAO));
        Article article = articleRepository.save(new Article(user, true, 2024, 1, "contents"));
        ArticleLike articleLike = articleLikeRepository.save(new ArticleLike(article, user));

        //when : 자신의 비공개 기록장에 좋아요 취소 요청 시
        String url = "/article/" + TsidUtil.toString(article.getId()) + "/like";
        ExtractableResponse<Response> response = delete(url, user.getId(), null);

        //then : 좋아요는 성공적으로 삭제되어야 한다
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        softAssertions.assertThat(articleLikeRepository.findById(articleLike.getId())).isEmpty();
        softAssertions.assertThat(articleLikeRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("성공 - 친구의 공개 기록장")
    void test3() {
        //given
        User user = userRepository.save(new User("nickname", "https://test.org/user0.jpg", "user1@email.com", LoginType.KAKAO));
        User friend = userRepository.save(new User("friend", "https://test.org/user1.jpg", "user2@email.com", LoginType.KAKAO));
        Article friendArticle = articleRepository.save(new Article(friend, true, 2024, 1, "contents"));
        makeFriendships(user, friend);
        ArticleLike articleLike = articleLikeRepository.save(new ArticleLike(friendArticle, user));

        //when : 친구의 공개 기록장에 좋아요 취소 요청 시
        String url = "/article/" + TsidUtil.toString(friendArticle.getId()) + "/like";
        ExtractableResponse<Response> response = delete(url, user.getId(), null);

        //then : 좋아요는 성공적으로 삭제되어야 한다
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        softAssertions.assertThat(articleLikeRepository.findById(articleLike.getId())).isEmpty();
        softAssertions.assertThat(articleLikeRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("실패 - 친구의 비공개 기록장")
    void test4() {
        //given
        User user = userRepository.save(new User("nickname1", "https://test.org/user0.jpg", "user1@email.com", LoginType.KAKAO));
        User friend = userRepository.save(new User("friend", "https://test.org/user1.jpg", "user2@email.com", LoginType.KAKAO));
        Article friendArticle = articleRepository.save(new Article(friend, false, 2024, 1, "contents"));
        makeFriendships(user, friend);
        ArticleLike articleLike = articleLikeRepository.save(new ArticleLike(friendArticle, user));

        //when : 친구의 비공개 기록장에 좋아요 취소 요청 시
        String url = "/article/" + TsidUtil.toString(friendArticle.getId()) + "/like";
        ExtractableResponse<Response> response = delete(url, user.getId(), null);

        //then : 요청은 거절되어야 한다
        softAssertions.assertThat(response.statusCode()).isEqualTo(403);
        softAssertions.assertThat(articleLikeRepository.findById(articleLike.getId())).isPresent();
        softAssertions.assertThat(articleLikeRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("실패 - 타인의 기록장")
    void test5() {
        //given : 상대방의 기록장에 좋아요를 눌렀었지만 현재는 친구가 아닌 경우
        User user = userRepository.save(new User("nickname1", "https://test.org/user0.jpg", "user1@email.com", LoginType.KAKAO));
        User stranger = userRepository.save(
            new User("nickname2", "https://test.org/user1.jpg", "user2@email.com", LoginType.KAKAO));
        Article strangerArticle = articleRepository.save(new Article(stranger, true, 2024, 1, "contents"));
        makeFriendships(user, stranger);
        ArticleLike articleLike = articleLikeRepository.save(new ArticleLike(strangerArticle, user));
        cancelFriendships(user, stranger);

        //when : 해당 좋아요에 대한 취소 요청 시
        String url = "/article/" + TsidUtil.toString(strangerArticle.getId()) + "/like";
        ExtractableResponse<Response> response = delete(url, user.getId(), null);

        //then : 요청은 거절되어야 한다
        softAssertions.assertThat(response.statusCode()).isEqualTo(403);
        softAssertions.assertThat(articleLikeRepository.findById(articleLike.getId())).isPresent();
        softAssertions.assertThat(articleLikeRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("성공 - 친구의 취소 중복 요청")
    void test6() {
        //given : 회원이 친구의 공개 기록장에 좋아요를 누르지 않은 상태에서
        User user = userRepository.save(new User("nickname1", "https://test.org/user0.jpg", "user1@email.com", LoginType.KAKAO));
        User friend = userRepository.save(new User("friend", "https://test.org/user1.jpg", "user2@email.com", LoginType.KAKAO));
        Article friendArticle = articleRepository.save(new Article(friend, true, 2024, 1, "contents"));
        makeFriendships(user, friend);

        //when : 친구의 공개 기록장에 좋아요 취소 요청 시
        String url = "/article/" + TsidUtil.toString(friendArticle.getId()) + "/like";
        ExtractableResponse<Response> response = delete(url, user.getId(), null);

        //then : 요청은 성공한다
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        softAssertions.assertThat(articleLikeRepository.count()).isEqualTo(0);
    }

}
