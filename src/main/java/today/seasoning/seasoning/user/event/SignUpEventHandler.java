package today.seasoning.seasoning.user.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class SignUpEventHandler {

    @Value("${OFFICIAL_ACCOUNT_USER_ID}")
    private Long officialAccountUserId;

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void addOfficialAccountFriend(SignUpEvent event) {
        User signUpUser = event.getSignUpUser();
        User officialUser = userRepository.findByIdOrElseThrow(officialAccountUserId);
        friendshipRepository.save(new Friendship(signUpUser, officialUser));
        friendshipRepository.save(new Friendship(officialUser, signUpUser));
        log.info("Sign Up Event - User {}", signUpUser.getId());
    }
}
