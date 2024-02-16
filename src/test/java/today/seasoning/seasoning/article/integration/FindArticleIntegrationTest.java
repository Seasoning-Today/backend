package today.seasoning.seasoning.article.integration;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import today.seasoning.seasoning.BaseIntegrationTest;
import today.seasoning.seasoning.article.domain.Article;
import today.seasoning.seasoning.article.domain.ArticleImage;
import today.seasoning.seasoning.article.domain.ArticleImageRepository;
import today.seasoning.seasoning.article.domain.ArticleLike;
import today.seasoning.seasoning.article.domain.ArticleLikeRepository;
import today.seasoning.seasoning.article.domain.ArticleRepository;
import today.seasoning.seasoning.article.dto.ArticleImageResponse;
import today.seasoning.seasoning.article.dto.ArticleResponse;
import today.seasoning.seasoning.article.dto.ArticleResponse.ArticleResponseBuilder;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.friendship.domain.Friendship;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;
import today.seasoning.seasoning.user.dto.UserProfileResponse;

@DisplayName("기록장 조회 통합 테스트")
public class FindArticleIntegrationTest extends BaseIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ArticleImageRepository articleImageRepository;

    @Autowired
    ArticleLikeRepository articleLikeRepository;

    @Autowired
    FriendshipRepository friendshipRepository;

    @InjectSoftAssertions
    SoftAssertions softAssertions;

    static final String baseUrl = "/article/";

    List<User> users;

    @BeforeEach
    void initMockUsers() {
        users = userRepository.saveAll(List.of(
            new User("userNickname0", "https://test.org/user0.jpg", "user0@email.com", LoginType.KAKAO),
            new User("userNickname1", "https://test.org/user1.jpg", "user1@email.com", LoginType.KAKAO),
            new User("userNickname2", "https://test.org/user2.jpg", "user2@email.com", LoginType.KAKAO),
            new User("userNickname3", "https://test.org/user3.jpg", "user3@email.com", LoginType.KAKAO)
        ));
    }

    @Test
    @DisplayName("성공 - 자신의 비공개 기록장 조회")
    void test() throws Exception {
        //given : 회원이 기록장을 비공개로 작성했다면
        User author = users.get(0);

        boolean published = false;

        Article article = createArticle(author, published);
        List<ArticleImage> articleImages = createArticleImages(article);
        List<ArticleLike> articleLikes = createArticleLikes(article, users.subList(0, 2));

        //when : 자신의 비공개 기록장을 조회했을 때
        String url = baseUrl + TsidUtil.toString(article.getId());

        ExtractableResponse<Response> response = get(url, author.getId());
        ArticleResponse articleResponse = response.as(new TypeRef<>() {
        });

        //then : 조회는 성공한다
        softAssertions.assertThat(response.statusCode())
            .as("상태 코드는 200이어야 한다")
            .isEqualTo(200);

        ArticleResponseBuilder expectedArticleResponse = ArticleResponse.builder()
            .published(published)
            .year(article.getCreatedYear())
            .term(article.getCreatedTerm())
            .contents(article.getContents())
            .images(ArticleImageResponse.build(articleImages))
            .profile(UserProfileResponse.build(author))
            .likesCount(articleLikes.size())
            .userLikes(true);

        softAssertions.assertThat(articleResponse)
            .as("조회된 기록장의 내용은 expectedArticleResponse와 동일해야 한다")
            .usingRecursiveComparison()
            .isEqualTo(expectedArticleResponse);
    }

    @Test
    @DisplayName("성공 - 친구 기록장 조회")
    void test2() throws Exception {
        //given : user와 friend가 친구이고, friend가 기록장을 공개 상태로 작성했다면
        User user = users.get(0);
        User friend = users.get(1);

        createFriendships(user, friend);

        boolean published = true;
        Article friendArticle = createArticle(friend, published);
        List<ArticleImage> friendArticleImages = createArticleImages(friendArticle);


        //when : user가 friend의 공개된 기록장을 조회했을 때
        String url = baseUrl + TsidUtil.toString(friendArticle.getId());

        ExtractableResponse<Response> response = get(url, user.getId());
        ArticleResponse articleResponse = response.as(new TypeRef<>() {
        });

        //then : 조회는 성공한다
        softAssertions.assertThat(response.statusCode())
            .as("상태 코드는 200이어야 한다")
            .isEqualTo(200);

        ArticleResponseBuilder expectedArticleResponse = ArticleResponse.builder()
            .published(published)
            .year(friendArticle.getCreatedYear())
            .term(friendArticle.getCreatedTerm())
            .contents(friendArticle.getContents())
            .images(ArticleImageResponse.build(friendArticleImages))
            .profile(UserProfileResponse.build(friend))
            .likesCount(0)
            .userLikes(false);

        softAssertions.assertThat(articleResponse)
            .as("조회된 기록장의 내용은 expectedArticleResponse와 동일해야 한다")
            .usingRecursiveComparison()
            .isEqualTo(expectedArticleResponse);
    }

    @Test
    @DisplayName("실패 - 친구의 기록장이 아닌 경우")
    void test3() throws Exception {
        //given : user와 author가 친구가 아니고, author가 기록장을 공개 상태로 작성했다면
        User user = users.get(0);
        User author = users.get(1);

        boolean published = true;
        Article article = createArticle(author, published);

        //when : user가 author의 기록장을 조회했을 때
        String url = baseUrl + TsidUtil.toString(article.getId());
        ExtractableResponse<Response> response = get(url, user.getId());

        //then : 조회는 실패한다
        softAssertions.assertThat(response.statusCode())
            .as("상태 코드는 403이어야 한다")
            .isEqualTo(403);
    }

    @Test
    @DisplayName("실패 - 친구의 비공개 기록장 조회")
    void test4() throws Exception {
        //given : user와 friend가 친구이고, friend가 기록장을 비공개 상태로 작성했다면
        User user = users.get(0);
        User friend = users.get(1);

        createFriendships(user, friend);

        boolean published = false;
        Article friendArticle = createArticle(friend, published);

        //when : user가 friend의 기록장을 조회했을 때
        String url = baseUrl + TsidUtil.toString(friendArticle.getId());
        ExtractableResponse<Response> response = get(url, user.getId());

        //then : 조회는 실패한다
        softAssertions.assertThat(response.statusCode())
            .as("상태 코드는 403이어야 한다")
            .isEqualTo(403);
    }

    private Article createArticle(User author, boolean published) {
        return articleRepository.save(Article.builder()
            .user(author)
            .published(published)
            .createdTerm(1)
            .createdYear(2024)
            .contents("Contents")
            .build());
    }

    private List<ArticleImage> createArticleImages(Article article) {
        return articleImageRepository.saveAll(List.of(
            new ArticleImage(article, "test1.jpg", "https://test1.org/test.jpg", 1),
            new ArticleImage(article, "test2.jpg", "https://test2.org/test.jpg", 2)
        ));
    }


    private List<ArticleLike> createArticleLikes(Article article, List<User> users) {
        return users.stream()
            .map(user -> articleLikeRepository.save(new ArticleLike(article, user)))
            .collect(Collectors.toList());
    }

    private void createFriendships(User user1, User user2) {
        friendshipRepository.save(new Friendship(user1, user2));
        friendshipRepository.save(new Friendship(user2, user1));
    }
}
