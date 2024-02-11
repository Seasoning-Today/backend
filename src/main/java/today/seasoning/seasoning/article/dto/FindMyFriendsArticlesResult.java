package today.seasoning.seasoning.article.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(title = "친구 기록장 목록 조회 결과")
public class FindMyFriendsArticlesResult {

	private final UserProfileDto profile;
	private final FriendArticleDto article;

	public FindMyFriendsArticlesResult(UserProfileDto profile,
		FriendArticleDto article) {
		this.profile = profile;
		this.article = article;
	}
}
