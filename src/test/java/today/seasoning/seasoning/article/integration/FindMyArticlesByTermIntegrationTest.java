package today.seasoning.seasoning.article.integration;

import static java.util.stream.Collectors.toList;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import today.seasoning.seasoning.BaseIntegrationTest;
import today.seasoning.seasoning.article.domain.Article;
import today.seasoning.seasoning.article.domain.ArticleRepository;
import today.seasoning.seasoning.article.dto.ArticlePreviewResponse;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@DisplayName("절기별 기록장 조회")
public class FindMyArticlesByTermIntegrationTest extends BaseIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArticleRepository articleRepository;

    @InjectSoftAssertions
    SoftAssertions softAssertions;

    String url = "/article/list/term";

    User user;
    List<Article> articles;

    @BeforeEach
    void init() {
        user = userRepository.save(new User("user", "image", "email@test.org", LoginType.KAKAO));

        articles = articleRepository.saveAll(List.of(
            new Article(user, true, 2023, 1, null),
            new Article(user, true, 2023, 5, ""),
            new Article(user, true, 2024, 1, ""),
            new Article(user, true, 2024, 7, ""),
            new Article(user, true, 2025, 1, "")
        ));
    }

    @Test
    @DisplayName("절기 미선택 시, 전체 기록장을 최신순으로 조회한다")
    void test1() {
        //given
        int term = 0;

        //when
        HashMap<String, Object> params = new HashMap<>();
        params.put("term", term);

        ExtractableResponse<Response> response = get(url, user.getId(), params);
        List<ArticlePreviewResponse> articleResponses = response.body().as(new TypeRef<>() {
        });

        //then
        List<ArticlePreviewResponse> expected = articles.stream()
            .sorted(Comparator.comparingLong(Article::getId).reversed())
            .map(ArticlePreviewResponse::build)
            .collect(toList());

        softAssertions.assertThat(articleResponses)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("절기 선택 시, 해당 절기의 기록장을 최신순으로 조회한다")
    void test2() {
        //given
        int term = 1;

        //when
        HashMap<String, Object> params = new HashMap<>();
        params.put("term", term);

        ExtractableResponse<Response> response = get(url, user.getId(), params);
        List<ArticlePreviewResponse> articleResponses = response.body().as(new TypeRef<>() {
        });

        //then
        softAssertions.assertThat(articleResponses.stream().allMatch(a -> a.getTerm() == term)).isTrue();

        softAssertions.assertThat(articleResponses.stream().map(ArticlePreviewResponse::getYear).collect(toList()))
            .usingRecursiveComparison()
            .isEqualTo(List.of(2025, 2024, 2023));
    }
}
