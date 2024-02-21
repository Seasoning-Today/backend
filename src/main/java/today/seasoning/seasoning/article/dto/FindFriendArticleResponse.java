package today.seasoning.seasoning.article.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.article.domain.Article;
import today.seasoning.seasoning.user.dto.UserProfileResponse;

@Getter
@RequiredArgsConstructor
public class FindFriendArticleResponse {

    private final UserProfileResponse profile;
    private final ArticlePreviewResponse article;

    public static FindFriendArticleResponse build(Article article) {
        return new FindFriendArticleResponse(
            UserProfileResponse.build(article.getUser()),
            ArticlePreviewResponse.build(article));
    }
}
