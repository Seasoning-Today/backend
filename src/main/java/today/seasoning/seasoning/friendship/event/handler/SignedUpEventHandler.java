package today.seasoning.seasoning.friendship.event.handler;

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
import today.seasoning.seasoning.user.event.SignedUpEvent;

@Component
@RequiredArgsConstructor
public class SignedUpEventHandler {

    @Value("${OFFICIAL_ACCOUNT_USER_ID}")
    private Long officialAccountUserId;

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void addOfficialAccountFriend(SignedUpEvent event) {
        User signedUpUser = event.getSignedUpUser();
        User officialUser = userRepository.findByIdOrElseThrow(officialAccountUserId);
        friendshipRepository.save(new Friendship(signedUpUser, officialUser));
        friendshipRepository.save(new Friendship(officialUser, signedUpUser));
    }
}
