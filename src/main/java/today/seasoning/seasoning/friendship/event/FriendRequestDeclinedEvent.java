package today.seasoning.seasoning.friendship.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FriendRequestDeclinedEvent {

    private final Long fromUserId;
    private final Long toUserId;

}
