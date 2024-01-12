package today.seasoning.seasoning.common.token.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenInfo {

    private final String accessToken;
    private final String refreshToken;

}
