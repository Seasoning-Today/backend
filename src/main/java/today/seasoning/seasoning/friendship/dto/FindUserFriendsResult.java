package today.seasoning.seasoning.friendship.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.user.domain.User;

@Getter
@RequiredArgsConstructor
public class FindUserFriendsResult {

    private final String nickname;
    private final String accountId;
    private final String profileImageUrl;

    public static FindUserFriendsResult build(User friend) {
        return new FindUserFriendsResult(
            friend.getNickname(),
            friend.getAccountId(),
            friend.getProfileImageUrl());
    }
}
