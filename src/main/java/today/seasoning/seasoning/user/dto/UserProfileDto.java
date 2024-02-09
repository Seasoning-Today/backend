package today.seasoning.seasoning.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.user.domain.User;

@Getter
@Setter
@NoArgsConstructor
public class UserProfileDto {

	private String id;
	private String nickname;
	private String accountId;
	private String profileImageUrl;

	public UserProfileDto(Long id, String nickname, String accountId, String profileImageUrl) {
		this.id = TsidUtil.toString(id);
		this.nickname = nickname;
		this.accountId = accountId;
		this.profileImageUrl = profileImageUrl;
	}

	public static UserProfileDto build(User user) {
		return new UserProfileDto(
			user.getId(),
			user.getNickname(),
			user.getAccountId(),
			user.getProfileImageUrl());
	}

}
