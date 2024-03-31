package today.seasoning.seasoning.article.integration;

import io.restassured.common.mapper.TypeRef;
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
import today.seasoning.seasoning.article.domain.ArticleRepository;
import today.seasoning.seasoning.article.dto.FindMyArticlesByYearResponse;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@DisplayName("연도별 기록장 조회")
public class FindMyArticlesByYearIntegrationTest extends BaseIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArticleRepository articleRepository;

    @InjectSoftAssertions
    SoftAssertions softAssertions;

    @Test
    @DisplayName("성공")
    void test() {
        //given
        User user = userRepository.save(new User("user", "image", "email@test.org", LoginType.KAKAO));

        // 2023년도 기록장
        Article article0 = articleRepository.save(new Article(user, true, 2023, 1, "2023-1"));
        Article article1 = articleRepository.save(new Article(user, true, 2023, 2, "2023-2"));
        // 2024년도 기록장 : article2 - 공개 기록장, article3 - 비공개 기록장
        Article article2 = articleRepository.save(new Article(user, true, 2024, 1, "2024-1"));
        Article article3 = articleRepository.save(new Article(user, false, 2024, 2, "2024-2"));
        // 2025년도 기록장
        Article article4 = articleRepository.save(new Article(user, true, 2025, 1, "2025-1"));

        //when: 2024년도에 작성된 기록장 목록을 요청 시
        ExtractableResponse<Response> response = get("/article/list/year/2024", user.getId());
        List<FindMyArticlesByYearResponse> actualResult = response.body().as(new TypeRef<>() {
        });

        //then: 2024년도에 작성된 기록장(article2, article3)의 식별자와 작성된 절기가 조회되어야 한다
        List<FindMyArticlesByYearResponse> expectedResult = List.of(
            new FindMyArticlesByYearResponse(TsidUtil.toString(article2.getId()), article2.getCreatedTerm()),
            new FindMyArticlesByYearResponse(TsidUtil.toString(article3.getId()), article3.getCreatedTerm())
        );

        softAssertions.assertThat(actualResult)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedResult);
    }
}
