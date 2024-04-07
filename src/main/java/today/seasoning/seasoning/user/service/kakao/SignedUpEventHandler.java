package today.seasoning.seasoning.user.service.kakao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import today.seasoning.seasoning.friendship.domain.Friendship;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@Component
@RequiredArgsConstructor
public class SignedUpEventHandler {
    @Value("${user.official}")
    private String official;

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addFriendshipOfficialAccount(SignedUpEvent event) {
        User officialUser = userRepository.findByIdOrElseThrow(Long.parseLong(official));
        User signUpUser = event.getSignUpUser();
        friendshipRepository.save(new Friendship(signUpUser, officialUser));
        friendshipRepository.save(new Friendship(officialUser, signUpUser));
    }
}
