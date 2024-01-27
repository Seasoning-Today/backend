package today.seasoning.seasoning.user.domain;

import org.springframework.http.HttpStatus;
import today.seasoning.seasoning.common.exception.CustomException;

public class AccountId {

    private final String accountId;

    public AccountId(String accountId) {
        this.accountId = accountId;
        selfValidate();
    }

    /*
    [아이디 규칙]
    - 5글자 이상, 20글자 이하
    - 영문 소문자, 숫자, 밑줄('_') 및 점('.')으로 구성
    - 점으로 시작하거나 끝날 수 없습니다.
    - 연속된 점은 포함될 수 없습니다.
    */
    private void selfValidate() {
        String upperCases = ".*[A-Z].*";
        String regex = "^(?!.*\\.\\.)(?!.*\\.$)[^\\W][\\w.]{4,19}$";

        if (accountId == null || accountId.matches(upperCases) || !accountId.matches(regex)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Invalid ID Format");
        }
    }

    public String get() {
        return accountId;
    }
}
