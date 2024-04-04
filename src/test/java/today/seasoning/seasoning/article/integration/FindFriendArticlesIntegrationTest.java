package today.seasoning.seasoning.article.integration;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import today.seasoning.seasoning.BaseIntegrationTest;
import today.seasoning.seasoning.article.domain.Article;
import today.seasoning.seasoning.article.domain.ArticleImage;
import today.seasoning.seasoning.article.domain.ArticleImageRepository;
import today.seasoning.seasoning.article.domain.ArticleRepository;
import today.seasoning.seasoning.article.dto.ArticlePreviewResponse;
import today.seasoning.seasoning.article.dto.FindFriendArticleResponse;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.friendship.domain.Friendship;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;
import today.seasoning.seasoning.user.dto.UserProfileResponse;

@DisplayName("친구 기록장 조회")
public class FindFriendArticlesIntegrationTest extends BaseIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    FriendshipRepository friendshipRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ArticleImageRepository articleImageRepository;

    @InjectSoftAssertions
    SoftAssertions softAssertions;

    private static final String url = "/article/friends";

    private void makeFriendships(User user, User friend) {
        friendshipRepository.save(new Friendship(user, friend));
        friendshipRepository.save(new Friendship(friend, user));
    }

    @Test
    @DisplayName("기록장 공개/비공개 테스트")
    void test() {
        //given: user가 friend1과 friend2와 친구인 경우
        User user = userRepository.save(new User("user", "https://test.org/user.jpg", "user@test.org", LoginType.KAKAO));
        User friend1 = userRepository.save(new User("friend1", "https://test.org/friend1.jpg", "friend1@test.org", LoginType.KAKAO));
        User friend2 = userRepository.save(new User("friend2", "https://test.org/friend2.jpg", "friend2@test.org", LoginType.KAKAO));
        makeFriendships(user, friend1);
        makeFriendships(user, friend2);

        // friend1의 기록장
        // article1: 공개 기록장 / 이미지 2개
        Article article1 = articleRepository.save(new Article(friend1, true, 2024, 1, "[{\"type\":\"single\",\"text\":\"article1\"}]"));
        ArticleImage articleImage1_1 = articleImageRepository.save(new ArticleImage(article1, "article1_1.jpg", "https://test.org/article1_1.jpg", 1));
        ArticleImage articleImage1_2 = articleImageRepository.save(new ArticleImage(article1, "article1_2.jpg", "https://test.org/article1_2.jpg", 2));
        // article2: 공개 기록장 / 이미지 없음
        Article article2 = articleRepository.save(new Article(friend1, true, 2024, 2, "[{\"type\":\"single\",\"text\":\"article2\"}]"));
        // article3: 비공개 기록장 / 이미지 1개
        Article article3 = articleRepository.save(new Article(friend1, false, 2024, 3, "[{\"type\":\"single\",\"text\":\"article3\"}]"));
        ArticleImage articleImage3 = articleImageRepository.save(new ArticleImage(article3, "article3.jpg", "https://test.org/article3.jpg", 1));

        // friend2의 기록장
        // article4: 공개 기록장 / 이미지 2개
        Article article4 = articleRepository.save(new Article(friend2, true, 2024, 1, "[{\"type\":\"single\",\"text\":\"article4\"}]"));
        ArticleImage articleImage4_1 = articleImageRepository.save(new ArticleImage(article4, "article4_1.jpg", "https://test.org/article4_1.jpg", 1));
        ArticleImage articleImage4_2 = articleImageRepository.save(new ArticleImage(article4, "article4_2.jpg", "https://test.org/article4_2.jpg", 2));
        // article5 : 비공개 기록장 / 이미지 없음
        Article article5 = articleRepository.save(new Article(friend2, false, 2024, 2, "[{\"type\":\"single\",\"text\":\"article5\"}]"));


        //when: 친구 기록장 조회 시
        ExtractableResponse<Response> response = get(url, user.getId());
        List<FindFriendArticleResponse> actualResult = response.body().as(new TypeRef<>() {
        });


        //then: 친구의 공개 기록장이 최신순으로 조회되어야 한다
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);

        // friend1 프로필 정보
        UserProfileResponse friend1Profile = new UserProfileResponse(friend1.getId(), friend1.getNickname(), friend1.getAccountId(), friend1.getProfileImageUrl());

        // 응답 1: friend1의 공개 기록장 - article1
        // 이미지 주소 : 첫번째 이미지(sequence가 작은 이미지)의 url
        FindFriendArticleResponse expectedResponseOfArticle1 = new FindFriendArticleResponse(friend1Profile,
            new ArticlePreviewResponse(TsidUtil.toString(article1.getId()), 2024, 1, "article1", articleImage1_1.getUrl()));

        // 응답 2: friend1의 공개 기록장 - article2
        // 이미지 주소 : null (이미지 없음)
        FindFriendArticleResponse expectedResponseOfArticle2 = new FindFriendArticleResponse(friend1Profile,
            new ArticlePreviewResponse(TsidUtil.toString(article2.getId()), 2024, 2, "article2", null));

        // friend2 프로필 정보
        UserProfileResponse friend2Profile = new UserProfileResponse(friend2.getId(), friend2.getNickname(), friend2.getAccountId(), friend2.getProfileImageUrl());

        // 응답 3: friend2의 공개 기록장 - article4
        // 이미지 주소 : 첫번째 이미지(sequence가 작은 이미지)의 url
        FindFriendArticleResponse expectedResponseOfArticle4 = new FindFriendArticleResponse(friend2Profile,
            new ArticlePreviewResponse(TsidUtil.toString(article4.getId()), 2024, 1, "article4", articleImage4_1.getUrl()));

        // 정상 응답 : Article.id의 내림차순으로 정렬된 응답 (article4 -> article2 -> article1)
        List<FindFriendArticleResponse> expectedResult = List.of(expectedResponseOfArticle4 ,expectedResponseOfArticle2, expectedResponseOfArticle1);
        softAssertions.assertThat(actualResult).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("페이지네이션 테스트")
    void test2() {
        //given: user와 friend가 친구이고, friend의 공개 기록장이 총 9개일 때
        final int TOTAL = 9;

        User user = userRepository.save(new User("user", "https://test.org/user.jpg", "user@test.org", LoginType.KAKAO));
        User friend = userRepository.save(new User("friend", "https://test.org/friend.jpg", "friend@test.org", LoginType.KAKAO));
        makeFriendships(user, friend);

        articleRepository.saveAll(List.of(
            new Article(friend, true, 2024, 1, "[{\"type\":\"single\",\"text\":\"article1\"}]"),
            new Article(friend, true, 2024, 2, "[{\"type\":\"single\",\"text\":\"article2\"}]"),
            new Article(friend, true, 2024, 3, "[{\"type\":\"single\",\"text\":\"article3\"}]"),
            new Article(friend, true, 2024, 4, "[{\"type\":\"single\",\"text\":\"article4\"}]"),
            new Article(friend, true, 2024, 5, "[{\"type\":\"single\",\"text\":\"article5\"}]"),
            new Article(friend, true, 2024, 6, "[{\"type\":\"single\",\"text\":\"article6\"}]"),
            new Article(friend, true, 2024, 7, "[{\"type\":\"single\",\"text\":\"article7\"}]"),
            new Article(friend, true, 2024, 8, "[{\"type\":\"single\",\"text\":\"article8\"}]"),
            new Article(friend, true, 2024, 9, "[{\"type\":\"single\",\"text\":\"article9\"}]")
        ));


        //when: 첫번째 페이지 조회 시
        final int PAGE_SIZE = 5;

        HashMap<String, Object> params1 = new HashMap<>();
        params1.put("size", PAGE_SIZE);

        ExtractableResponse<Response> firstResponse = get(url, user.getId(), params1);
        List<FindFriendArticleResponse> firstPage = firstResponse.body().as(new TypeRef<>() {
        });

        //then: Article.id의 내림차순으로 정렬된 5개의 기록장(article9 ~ article5)이 조회된다
        softAssertions.assertThat(firstResponse.statusCode()).isEqualTo(200);
        softAssertions.assertThat(firstPage.size()).isEqualTo(PAGE_SIZE);
        softAssertions.assertThat(checkOrderedByArticleIdDesc(firstPage)).isTrue();
        softAssertions.assertThat(firstPage.stream().allMatch(item -> item.getArticle().getTerm() >= 5)).isTrue();


        //when: 두번째 페이지 조회 시 (size: 동일, lastId: 직전에 조회한 페이지에서의 마지막 기록장의 id)
        HashMap<String, Object> params2 = new HashMap<>();
        params2.put("size", PAGE_SIZE);
        params2.put("lastId", firstPage.get(PAGE_SIZE - 1).getArticle().getId());

        ExtractableResponse<Response> secondResponse = get(url, user.getId(), params2);
        List<FindFriendArticleResponse> secondPage = secondResponse.body().as(new TypeRef<>() {
        });

        //then: Article.id의 내림차순으로 정렬된 나머지 4개의 기록장(article4 ~ article1)이 조회된다
        softAssertions.assertThat(secondResponse.statusCode()).isEqualTo(200);
        softAssertions.assertThat(secondPage.size()).isEqualTo(TOTAL - PAGE_SIZE);
        softAssertions.assertThat(checkOrderedByArticleIdDesc(secondPage)).isTrue();
        softAssertions.assertThat(secondPage.stream().allMatch(item -> item.getArticle().getTerm() <= 4)).isTrue();
    }

    private boolean checkOrderedByArticleIdDesc(List<FindFriendArticleResponse> responses) {
        for(int index = 1; index < responses.size(); index++) {
            Long prevArticleId = TsidUtil.toLong(responses.get(index - 1).getArticle().getId());
            Long nextArticleId = TsidUtil.toLong(responses.get(index).getArticle().getId());

            if(prevArticleId < nextArticleId) {
                return false;
            }
        }
        return true;
    }

    @Test
    @DisplayName("친구가 없는 경우")
    void test3() {
        //given: 친구가 없는 경우
        User user = userRepository.save(new User("user", "https://test.org/user.jpg", "user@test.org", LoginType.KAKAO));
        articleRepository.save(new Article(user, true, 2024, 1, "[{\"type\":\"single\",\"text\":\"article\"}]"));

        //when: 친구의 기록장 조회 시
        ExtractableResponse<Response> response = get(url, user.getId());
        List<FindFriendArticleResponse> responseBody = response.body().as(new TypeRef<>() {
        });

        //then: 응답 결과는 없다
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        softAssertions.assertThat(responseBody.isEmpty()).isTrue();
    }
}
