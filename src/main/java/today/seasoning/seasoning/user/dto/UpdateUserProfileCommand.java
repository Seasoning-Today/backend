package today.seasoning.seasoning.user.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;
import today.seasoning.seasoning.user.domain.AccountId;
import today.seasoning.seasoning.user.domain.Nickname;

@Getter
public class UpdateUserProfileCommand {

	private final boolean imageModified;
	private final long userId;
	private final AccountId accountId;
	private final Nickname nickname;
	private final MultipartFile profileImage;

	public UpdateUserProfileCommand(boolean imageModified, long userId, String accountId, String nickname, MultipartFile profileImage) {
		this.imageModified = imageModified;
		this.userId = userId;
		this.accountId = new AccountId(accountId);
		this.nickname = new Nickname(nickname);
		this.profileImage = profileImage;
	}

	public String getAccountId() {
		return accountId.get();
	}

	public String getNickname() {
		return nickname.get();
	}
}
