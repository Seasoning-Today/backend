package today.seasoning.seasoning.article.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.article.domain.Article;
import today.seasoning.seasoning.user.dto.UserProfileResponse;

@Getter
@RequiredArgsConstructor
public class FindMyFriendsArticlesResult {

    private final UserProfileResponse profile;
    private final FriendArticleDto article;

    public static FindMyFriendsArticlesResult build(Article article) {
        return new FindMyFriendsArticlesResult(
            UserProfileResponse.build(article.getUser()),
            FriendArticleDto.build(article));
    }
}
