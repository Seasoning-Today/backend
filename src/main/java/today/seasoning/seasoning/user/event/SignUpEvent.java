package today.seasoning.seasoning.user.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.user.domain.User;

@Getter
@RequiredArgsConstructor
public class SignUpEvent {

    private final User signUpUser;

}
