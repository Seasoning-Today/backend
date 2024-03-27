package today.seasoning.seasoning;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import today.seasoning.seasoning.common.cipher.CipherUtil;

public class CipherUtilTest extends BaseIntegrationTest {

    @Autowired
    CipherUtil cipherUtil;

    @InjectSoftAssertions
    SoftAssertions softAssertions;

    @Test
    @DisplayName("암복호화 테스트")
    void test() {
        try {
            String content = "abc";
            String encoded = cipherUtil.encode(content);
            String decoded = cipherUtil.decode(encoded);
            softAssertions.assertThat(content).isNotEqualTo(encoded);
            softAssertions.assertThat(decoded).isEqualTo(content);
        } catch (Exception e) {
            softAssertions.fail(e.getMessage());
        }
    }

}
