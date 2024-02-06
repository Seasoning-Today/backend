package today.seasoning.seasoning.friendship.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.user.domain.User;

@Getter
@RequiredArgsConstructor
public class FindUserFriendsResponse {

    private final String id;
    private final String nickname;
    private final String accountId;
    private final String profileImageUrl;

    public static FindUserFriendsResponse build(User friend) {
        return new FindUserFriendsResponse(
            TsidUtil.toString(friend.getId()),
            friend.getNickname(),
            friend.getAccountId(),
            friend.getProfileImageUrl());
    }
}
