package today.seasoning.seasoning.user.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import today.seasoning.seasoning.common.exception.CustomException;

@DisplayName("닉네임 형식 검사")
class NicknameTest {

    @DisplayName("성공")
    @ParameterizedTest
    @ValueSource(strings = {"csct", "CSCT", "불타는전주비빔밥", "12345", "csct3434", "불타는전주비빔밥12"})
    public void success(String nickname) {
        assertThatCode(() -> new Nickname(nickname)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("실패 - Null")
    public void failedByNullity() {
        assertFailedValidation(null);
    }

    @Test
    @DisplayName("실패 - 빈 문자열")
    public void failedByEmptiness() {
        assertFailedValidation("");
    }

    @Test
    @DisplayName("실패 - 2글자 미만")
    public void failedByShortLength() {
        assertFailedValidation("가");
    }

    @Test
    @DisplayName("실패 - 10글자 초과")
    public void failedByLongLength() {
        assertFailedValidation("가나다라마ABCDE1");
    }

    @ParameterizedTest
    @DisplayName("실패 - 불허용 문자 '.'")
    @ValueSource(strings = {"cs.ct", "cs-ct", "cs_ct", "一二三", "csct!!!"})
    public void failedByInvalidCharacter(String nickname) {
        assertFailedValidation(nickname);
    }

    private void assertFailedValidation(String invalidNickname) {
        assertThatThrownBy(() -> new Nickname(invalidNickname))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.FORBIDDEN);
    }
}
