package today.seasoning.seasoning.common.cipher;

import javax.persistence.AttributeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CryptoConverter implements AttributeConverter<String, String> {

    private final CipherUtil cipherUtil;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return cipherUtil.encode(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return cipherUtil.decode(dbData);
    }
}
