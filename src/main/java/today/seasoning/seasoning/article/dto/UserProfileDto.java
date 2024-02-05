package today.seasoning.seasoning.article.dto;

import lombok.Getter;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.user.domain.User;

@Getter
public class UserProfileDto {

	private final String id;
	private final String nickname;
	private final String accountId;
	private final String image;

	public UserProfileDto(String id, String nickname, String accountId, String image) {
		this.id = id;
		this.nickname = nickname;
		this.accountId = accountId;
		this.image = image;
	}

	public static UserProfileDto build(User user) {
		return new UserProfileDto(
			TsidUtil.toString(user.getId()),
			user.getNickname(),
			user.getAccountId(),
			user.getProfileImageUrl());
	}
}
