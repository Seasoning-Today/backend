package today.seasoning.seasoning.friendship.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.common.enums.FriendshipStatus;
import today.seasoning.seasoning.user.domain.User;

@Getter
@RequiredArgsConstructor
public class SearchUserResult {

    private final String nickname;
    private final String image;
    private final String accountId;
    private FriendshipStatus friendshipStatus;

    public SearchUserResult(String nickname, String image, String accountId, FriendshipStatus friendshipStatus) {
        this.nickname = nickname;
        this.image = image;
        this.accountId = accountId;
        this.friendshipStatus = friendshipStatus;
    }

    public static SearchUserResult build(User friend, FriendshipStatus friendshipStatus) {
        return new SearchUserResult(
            friend.getNickname(),
            friend.getProfileImageUrl(),
            friend.getAccountId(),
            friendshipStatus);
    }
}
