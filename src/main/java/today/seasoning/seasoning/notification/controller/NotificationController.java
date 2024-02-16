package today.seasoning.seasoning.notification.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import today.seasoning.seasoning.common.UserPrincipal;
import today.seasoning.seasoning.notification.dto.FindNotificationCommand;
import today.seasoning.seasoning.notification.dto.UserNotificationResponse;
import today.seasoning.seasoning.notification.service.CheckUnreadNotificationsExistService;
import today.seasoning.seasoning.notification.service.NotificationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

	private final NotificationService notificationService;
	private final CheckUnreadNotificationsExistService checkUnreadNotificationsExistService;

	@GetMapping
	public ResponseEntity<List<UserNotificationResponse>> findNotifications(
		@AuthenticationPrincipal UserPrincipal principal,
		@RequestParam(name = "lastId", defaultValue = "AzL8n0Y58m7") String lastId,
		@RequestParam(name = "size", defaultValue = "10") Integer pageSize
	) {
		FindNotificationCommand command = new FindNotificationCommand(principal.getId(), lastId, pageSize);
		List<UserNotificationResponse> notifications = notificationService.findNotifications(command);
		return ResponseEntity.ok(notifications);
	}

	@GetMapping("/new")
	public ResponseEntity<Boolean> checkUnreadNotificationsExist(@AuthenticationPrincipal UserPrincipal principal) {
		boolean result = checkUnreadNotificationsExistService.doService(principal.getId());
		return ResponseEntity.ok(result);
	}

}
