package today.seasoning.seasoning.notification.dto;

import java.time.LocalDateTime;

public interface UserNotificationProjectionInterface {

    Long getId();
    String getType();
    LocalDateTime getCreated();
    Long getUserId();
    String getUserNickname();
    String getUserAccountId();
    String getUserImageUrl();
    String getMessage();
    Boolean getIsRead();

}
