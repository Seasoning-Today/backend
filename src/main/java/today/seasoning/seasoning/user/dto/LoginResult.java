package today.seasoning.seasoning.user.dto;

import lombok.Getter;
import today.seasoning.seasoning.common.token.domain.TokenInfo;

@Getter
public class LoginResult {

	private final TokenInfo tokenInfo;
	private final boolean firstLogin;

	public LoginResult(TokenInfo tokenInfo, boolean firstLogin) {
		this.tokenInfo = tokenInfo;
		this.firstLogin = firstLogin;
	}
}
