package today.seasoning.seasoning.user.dto;

import lombok.Getter;
import today.seasoning.seasoning.common.token.domain.TokenInfo;

@Getter
public class LoginResponse {

    private final boolean firstLogin;
    private final TokenInfo tokenInfo;

    public LoginResponse(boolean firstLogin, TokenInfo tokenInfo) {
        this.firstLogin = firstLogin;
        this.tokenInfo = tokenInfo;
    }

    public static LoginResponse build(LoginResult loginResult) {
        return new LoginResponse(loginResult.isFirstLogin(), loginResult.getTokenInfo());
    }
}
