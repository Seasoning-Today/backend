package today.seasoning.seasoning.article.service;

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
import today.seasoning.seasoning.article.dto.FindCollageResponse;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@DisplayName("콜라주 조회 통합 테스트")
class FindCollageServiceTest extends BaseIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ArticleImageRepository articleImageRepository;

    @InjectSoftAssertions
    SoftAssertions softAssertions;

    @Test
    @DisplayName("성공")
    void test() {
        //given
        User user = userRepository.save(new User("nickname0", "https://test.org/user0.jpg", "user0@email.com", LoginType.KAKAO));

        // article0는 2023년에 작성, 나머지 기록장은 2024년에 작성
        Article article0 = articleRepository.save(new Article(user, false, 2023, 1, "article0"));
        Article article1 = articleRepository.save(new Article(user, true, 2024, 1, "article1"));
        Article article2 = articleRepository.save(new Article(user, false, 2024, 2, "article2"));
        Article article3 = articleRepository.save(new Article(user, true, 2024, 3, "article3"));
        Article article4 = articleRepository.save(new Article(user, true, 2024, 4, "article3"));

        // article4를 제외한 나머지 기록장에는 모두 이미지가 존재
        ArticleImage article0_image1 = articleImageRepository.save(new ArticleImage(article0, "image1.png", "http://test.com/image1.png", 1));
        ArticleImage article0_image2 = articleImageRepository.save(new ArticleImage(article0, "image2.png", "http://test.com/image2.png", 2));
        ArticleImage article1_image1 = articleImageRepository.save(new ArticleImage(article1, "image3.png", "http://test.com/image3.png", 1));
        ArticleImage article1_image2 = articleImageRepository.save(new ArticleImage(article1, "image4.png", "http://test.com/image4.png", 2));
        ArticleImage article2_image1 = articleImageRepository.save(new ArticleImage(article2, "image5.png", "http://test.com/image5.png", 2));
        ArticleImage article3_image1 = articleImageRepository.save(new ArticleImage(article3, "image6.png", "http://test.com/image6.png", 1));
        ArticleImage article3_image2 = articleImageRepository.save(new ArticleImage(article3, "image7.png", "http://test.com/image7.png", 4));

        //when: 2024년 콜라주 조회 요청 시
        HashMap<String, Object> params = new HashMap<>();
        params.put("year", 2024);

        ExtractableResponse<Response> response = get("/article/collage", user.getId(), params);
        List<FindCollageResponse> actualFindCollageResponses = response.body().as(new TypeRef<>() {
        });

        //then: 2024년에 작성된 기록장의 첫번째(sequence가 제일 작은) 이미지 주소가 조회되어야 한다
        // article1 - article1_image1
        // article2 - article2_image1
        // article3 - article3_image1
        // article4 - null
        List<FindCollageResponse> expectedFindCollageResponses = List.of(
            new FindCollageResponse(1, TsidUtil.toString(article1.getId()), article1_image1.getUrl()),
            new FindCollageResponse(2, TsidUtil.toString(article2.getId()), article2_image1.getUrl()),
            new FindCollageResponse(3, TsidUtil.toString(article3.getId()), article3_image1.getUrl()),
            new FindCollageResponse(4, TsidUtil.toString(article4.getId()), null));

        softAssertions.assertThat(actualFindCollageResponses)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedFindCollageResponses);
    }

}
