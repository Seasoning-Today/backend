package today.seasoning.seasoning.common.token.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonInclude(Include.NON_NULL)
@AllArgsConstructor
public class TokenInfo {

    private final String accessToken;
    private final String refreshToken;

}
