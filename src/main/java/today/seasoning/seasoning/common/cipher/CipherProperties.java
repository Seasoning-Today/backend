package today.seasoning.seasoning.common.cipher;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cipher")
public class CipherProperties {

    private String secretKey;
    private String algorithm;
    private String transformation;
    private String iv;
}
