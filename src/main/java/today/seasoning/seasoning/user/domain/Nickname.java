package today.seasoning.seasoning.user.domain;

import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import today.seasoning.seasoning.common.exception.CustomException;

public class Nickname {

    private final String nickname;

    public Nickname(String nickname) {
        this.nickname = nickname;
        selfValidate();
    }

    /*
    [닉네임 규칙]
    - 허용 문자 : 영문 소문자 & 대문자, 한글, 숫자
    - 허용 길이 : 2글자 이상, 10글자 이하
     */
    private void selfValidate() {
        String regex = "^[a-zA-Z0-9가-힣]{2,10}$";

        if (nickname == null || !Pattern.matches(regex, nickname)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "Invalid Nickname");
        }
    }

    public String get() {
        return nickname;
    }
}
