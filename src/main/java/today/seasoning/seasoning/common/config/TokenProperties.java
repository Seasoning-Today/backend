package today.seasoning.seasoning.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "token")
public class TokenProperties {

    private String secretKey;
    private Long accessTokenExpirationTimeMillis;
    private Long refreshTokenExpirationTimeMillis;
}
