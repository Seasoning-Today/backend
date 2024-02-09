package today.seasoning.seasoning.notification.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.user.dto.UserProfileDto;

@Getter
@RequiredArgsConstructor
public class UserNotificationResponse {

    private final String id;
    private final String type;
    private final LocalDateTime created;
    private final UserProfileDto profile;
    private final String message;
    private final boolean read;

    public static UserNotificationResponse build(UserNotificationProjectionInterface u) {
        return new UserNotificationResponse(
            TsidUtil.toString(u.getId()),
            u.getType(),
            u.getCreated(),
            new UserProfileDto(u.getUserId(), u.getUserNickname(), u.getUserAccountId(), u.getUserImageUrl()),
            u.getMessage(),
            u.getIsRead()
        );
    }
}
