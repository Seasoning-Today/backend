package today.seasoning.seasoning.user.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import today.seasoning.seasoning.common.exception.CustomException;

@DisplayName("아이디 형식 검사")
@ExtendWith(SoftAssertionsExtension.class)
class AccountIdTest {

    @DisplayName("성공")
    @ParameterizedTest
    @ValueSource(strings = {"abcde", "12345678901234567890", "csct3434", "_csct3434", "csct3434_", "csct__3434"})
    public void success(String accountId) {
        assertThatCode(() -> new AccountId(accountId)).doesNotThrowAnyException();
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
    @DisplayName("실패 - 대문자 포함")
    public void failedByUpperCase() {
        assertFailedValidation("CSCT3434");
    }

    @Test
    @DisplayName("실패 - 5글자 미만")
    public void failedByShortLength() {
        assertFailedValidation("abcd");
    }

    @Test
    @DisplayName("실패 - 20글자 초과")
    public void failedByLongLength() {
        assertFailedValidation("123456789/123456789/1");
    }

    @Test
    @DisplayName("실패 - 점으로 시작하는 아이디")
    public void failedByDotStarted() {
        assertFailedValidation(".csct3434");
    }

    @Test
    @DisplayName("실패 - 점으로 끝나는 아이디")
    public void failedByDotEnded() {
        assertFailedValidation("csct3434.");
    }

    @Test
    @DisplayName("실패 - 연속되는 점 포함")
    public void failedByConsecutiveDots() {
        assertFailedValidation("csct..3434");
    }

    @ParameterizedTest
    @DisplayName("실패 - 불허용 문자")
    @ValueSource(strings = {"가나다라마", "csct!", "一二三"})
    public void failedByInvalidCharacter(String accountId) {
        assertFailedValidation(accountId);
    }

    private void assertFailedValidation(String invalidAccountId) {
        assertThatThrownBy(() -> new AccountId(invalidAccountId))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
    }
}
