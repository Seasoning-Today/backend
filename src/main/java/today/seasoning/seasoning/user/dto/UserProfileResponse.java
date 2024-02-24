package today.seasoning.seasoning.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.user.domain.User;

@Getter
@Setter
@NoArgsConstructor
@Schema(title = "사용자 프로필 조회 응답")
public class UserProfileResponse {

	private String id;
	private String nickname;
	private String accountId;
	private String image;

	public UserProfileResponse(Long id, String nickname, String accountId, String image) {
		this.id = TsidUtil.toString(id);
		this.nickname = nickname;
		this.accountId = accountId;
		this.image = image;
	}

	public static UserProfileResponse build(User user) {
		return new UserProfileResponse(
			user.getId(),
			user.getNickname(),
			user.getAccountId(),
			user.getProfileImageUrl());
	}

}
