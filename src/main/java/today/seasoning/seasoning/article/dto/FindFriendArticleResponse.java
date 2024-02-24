package today.seasoning.seasoning.article.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.article.domain.Article;
import today.seasoning.seasoning.user.dto.UserProfileResponse;

@Getter
@RequiredArgsConstructor
@Schema(title = "친구 기록장 목록 조회 응답")
public class FindFriendArticleResponse {

    private final UserProfileResponse profile;
    private final ArticlePreviewResponse article;

    public static FindFriendArticleResponse build(Article article) {
        return new FindFriendArticleResponse(
            UserProfileResponse.build(article.getUser()),
            ArticlePreviewResponse.build(article));
    }
}
