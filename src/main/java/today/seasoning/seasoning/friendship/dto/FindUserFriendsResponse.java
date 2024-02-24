package today.seasoning.seasoning.friendship.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.user.domain.User;

@Getter
@RequiredArgsConstructor
@Schema(title = "친구 목록 조회 응답")
public class FindUserFriendsResponse {

    @Schema(description = "사용자 id")
    private final String id;
    @Schema(description = "사용자 닉네임", example = "이아린")
    private final String nickname;
    @Schema(description = "사용자 계정 id", example = "linggu")
    private final String accountId;
    @Schema(description = "사용자 프로필 이미지 url", example = "https://www.naver.com/")
    private final String profileImageUrl;

    public static FindUserFriendsResponse build(User friend) {
        return new FindUserFriendsResponse(
            TsidUtil.toString(friend.getId()),
            friend.getNickname(),
            friend.getAccountId(),
            friend.getProfileImageUrl());
    }
}
