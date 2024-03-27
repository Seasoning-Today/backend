package today.seasoning.seasoning.common.cipher;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CipherUtil {

    private final Cipher cipher;
    private final SecretKeySpec secretKeySpec;
    private final IvParameterSpec ivParameterSpec;

    @Autowired
    public CipherUtil(CipherProperties properties) throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.secretKeySpec = new SecretKeySpec(properties.getSecretKey().getBytes(UTF_8), properties.getAlgorithm());
        this.ivParameterSpec = new IvParameterSpec(properties.getIv().getBytes());
        this.cipher = Cipher.getInstance(properties.getTransformation());
    }

    public String encode(String plainText) {
        if (plainText == null) {
            return null;
        }

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(UTF_8));
            return Base64.encodeBase64String(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String decode(String encodedText) {
        if (encodedText == null) {
            return null;
        }

        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decoded = cipher.doFinal(Base64.decodeBase64(encodedText));
            return new String(decoded, UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
