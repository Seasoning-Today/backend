package today.seasoning.seasoning.article.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.article.domain.Article;
import today.seasoning.seasoning.article.domain.ArticleLike;
import today.seasoning.seasoning.user.dto.UserProfileResponse;

@Getter
@Builder
@RequiredArgsConstructor
@Schema(title = "기록장 조회 응답")
public class ArticleResponse {

	@Schema(description = "기록장 공개 여부", example = "True")
	private final boolean published;
	@Schema(description = "연도", example = "2024")
	private final int year;
	@Schema(description = "절기 순번", example = "2")
	private final int term;
	@Schema(description = "본문")
	private final String contents;
	@Schema(description = "본문 이미지 리스트")
	private final List<ArticleImageResponse> images;
	@Schema(description = "사용자 프로필")
	private final UserProfileResponse profile;
	@Schema(description = "좋아요 수", example = "3")
	private final int likesCount;
	@Schema(description = "좋아요 여부", example = "True")
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
