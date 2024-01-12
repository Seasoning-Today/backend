package today.seasoning.seasoning.user.dto;

import lombok.Getter;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.user.domain.User;

@Getter
public class SocialUserProfileDto {

	private final String nickname;
	private final String email;
	private final String profileImageUrl;

	public SocialUserProfileDto(String nickname, String email, String profileImageUrl) {
		this.nickname = nickname;
		this.email = email;
		this.profileImageUrl = profileImageUrl;
	}

	public User toEntity(LoginType loginType) {
		return new User(nickname, profileImageUrl, email, loginType);
	}
}
