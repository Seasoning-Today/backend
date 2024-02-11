package today.seasoning.seasoning.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.notification.domain.Notification;
import today.seasoning.seasoning.notification.domain.NotificationType;

@Getter
@NoArgsConstructor
@Schema(title = "알림")
public class NotificationDto {

	@Schema(description = "알림 아이디", required = true)
	private String id;
	@Schema(description = "알림 종류", required = true)
	private NotificationType type;
	@Schema(description = "JSON 응답", required = true)
	private String message;
	@Schema(description = "읽음 여부", required = true, example = "True")
	private boolean isRead;

	private NotificationDto(String id, NotificationType type, String message, boolean isRead) {
		this.id = id;
		this.type = type;
		this.message = message;
		this.isRead = isRead;
	}

	public static NotificationDto build(Notification notification) {
		return new NotificationDto(
			TsidUtil.toString(notification.getId()),
			notification.getType(),
			notification.getMessage(),
			notification.isRead());
	}
}
