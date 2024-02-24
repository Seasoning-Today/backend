package today.seasoning.seasoning.notification.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.user.dto.UserProfileResponse;

@Getter
@Builder
@RequiredArgsConstructor
@Schema(title = "알림 응답")
public class UserNotificationResponse {

    @Schema(description = "알림 id")
    private final String id;
    @Schema(description = "알림 종류")
    private final String type;
    @Schema(description = "알림 일자")
    private final LocalDateTime created;
    private final UserProfileResponse profile;
    @Schema(description = "알림 내용")
    private final String message;
    @Schema(description = "읽음 여부")
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
