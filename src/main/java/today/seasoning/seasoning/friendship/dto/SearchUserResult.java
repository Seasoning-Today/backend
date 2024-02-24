package today.seasoning.seasoning.friendship.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.common.enums.FriendshipStatus;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.user.domain.User;

@Getter
@RequiredArgsConstructor
@Schema(title = "친구 검색 응답")
public class SearchUserResult {

    @Schema(description = "사용자 id", example = "이아린")
    private final String id;
    @Schema(description = "사용자 닉네임", example = "이아린")
    private final String nickname;
    @Schema(description = "사용자 프로필 이미지 url", example = "https://www.naver.com/")
    private final String image;
    @Schema(description = "사용자 계정 id", example = "linguu")
    private final String accountId;
    @Schema(description = "현재 친구 관계", example = "FRIEND")
    private FriendshipStatus friendshipStatus;

    public SearchUserResult(String id, String nickname, String image, String accountId, FriendshipStatus friendshipStatus) {
        this.id = id;
        this.nickname = nickname;
        this.image = image;
        this.accountId = accountId;
        this.friendshipStatus = friendshipStatus;
    }

    public static SearchUserResult build(User friend, FriendshipStatus friendshipStatus) {
        return new SearchUserResult(
            TsidUtil.toString(friend.getId()),
            friend.getNickname(),
            friend.getProfileImageUrl(),
            friend.getAccountId(),
            friendshipStatus);
    }
}
