package today.seasoning.seasoning.article.dto;

import lombok.Getter;
import today.seasoning.seasoning.user.dto.UserProfileResponse;

@Getter
public class FindMyFriendsArticlesResult {

	private final UserProfileResponse profile;
	private final FriendArticleDto article;

	public FindMyFriendsArticlesResult(UserProfileResponse profile,
		FriendArticleDto article) {
		this.profile = profile;
		this.article = article;
	}
}
