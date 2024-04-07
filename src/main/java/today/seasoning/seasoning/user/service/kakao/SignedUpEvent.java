package today.seasoning.seasoning.user.service.kakao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import today.seasoning.seasoning.user.domain.User;

@Getter
@NoArgsConstructor
public class SignedUpEvent {
    private User signUpUser;
    public SignedUpEvent(User user) {
        this.signUpUser = user;
    }
}
