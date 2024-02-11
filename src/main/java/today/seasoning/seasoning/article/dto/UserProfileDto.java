package today.seasoning.seasoning.article.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.user.domain.User;

@Getter
@Schema(title = "사용자 프로필")
public class UserProfileDto {

	@Schema(description = "사용자 id", required = true)
	private final String id;
	@Schema(description = "사용자 닉네임", required = true)
	private final String nickname;
	@Schema(description = "사용자 계정 id", required = true)
	private final String accountId;
	@Schema(description = "사용자 프로필 이미지")
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
