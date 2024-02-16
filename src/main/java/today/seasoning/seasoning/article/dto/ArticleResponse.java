package today.seasoning.seasoning.article.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.article.domain.Article;
import today.seasoning.seasoning.article.domain.ArticleLike;
import today.seasoning.seasoning.user.dto.UserProfileResponse;

@Getter
@Builder
@RequiredArgsConstructor
public class ArticleResponse {

	private final boolean published;
	private final int year;
	private final int term;
	private final String contents;
	private final List<ArticleImageResponse> images;
	private final UserProfileResponse profile;
	private final int likesCount;
	private final boolean userLikes;

	public static ArticleResponse build(Long userId, Article article) {
		return new ArticleResponse(
			article.isPublished(),
			article.getCreatedYear(),
			article.getCreatedTerm(),
			article.getContents(),
			ArticleImageResponse.build(article.getArticleImages()),
			UserProfileResponse.build(article.getUser()),
			article.getArticleLikes().size(),
			article.getArticleLikes().stream().map(ArticleLike::getUser).anyMatch(user -> user.getId().equals(userId)));
	}
}
