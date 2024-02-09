package today.seasoning.seasoning.article.dto;

import lombok.Getter;
import today.seasoning.seasoning.user.dto.UserProfileDto;

@Getter
public class FindMyFriendsArticlesResult {

	private final UserProfileDto profile;
	private final FriendArticleDto article;

	public FindMyFriendsArticlesResult(UserProfileDto profile,
		FriendArticleDto article) {
		this.profile = profile;
		this.article = article;
	}
}
