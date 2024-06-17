package today.seasoning.seasoning.user.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.user.domain.User;

@Getter
@RequiredArgsConstructor
public class SignedUpEvent {

    private final User signedUpUser;

}
