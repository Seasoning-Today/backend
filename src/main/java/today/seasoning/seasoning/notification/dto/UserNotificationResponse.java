package today.seasoning.seasoning.notification.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.user.dto.UserProfileResponse;

@Getter
@Builder
@RequiredArgsConstructor
public class UserNotificationResponse {

    private final String id;
    private final String type;
    private final LocalDateTime created;
    private final UserProfileResponse profile;
    private final String message;
    private final boolean read;

    public static UserNotificationResponse build(UserNotificationProjectionInterface u) {
        return new UserNotificationResponse(
            TsidUtil.toString(u.getId()),
            u.getType(),
            u.getCreated(),
            new UserProfileResponse(u.getUserId(), u.getUserNickname(), u.getUserAccountId(), u.getUserImageUrl()),
            u.getMessage(),
            u.getIsRead()
        );
    }
}
