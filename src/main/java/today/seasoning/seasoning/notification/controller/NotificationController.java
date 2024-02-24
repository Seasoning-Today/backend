package today.seasoning.seasoning.notification.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import today.seasoning.seasoning.notification.service.FindNotificationsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
@Tag(name = "Notification", description = "알림 API Document")
public class NotificationController {

	private final FindNotificationsService findNotificationsService;
	private final CheckUnreadNotificationsExistService checkUnreadNotificationsExistService;

	@GetMapping
	@Operation(summary = "알림 조회", description = "사용자의 알림을 조회합니다.", responses = {
			@ApiResponse(responseCode = "200", description = "알림 조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserNotificationResponse.class)))),
			@ApiResponse(responseCode = "404", description = "알림 조회 실패 (사용자 조회 실패)", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
	}, parameters = {
			@Parameter(name = "lastId", description = "마지막으로 조회한 알림의 아이디", example = "AzL8n0Y58m7", in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "AzL8n0Y58m7")),
			@Parameter(name = "size", description = "페이지 크기", example = "10", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10"))
	})
	public ResponseEntity<List<UserNotificationResponse>> findNotifications(
		@AuthenticationPrincipal UserPrincipal principal,
		@RequestParam(name = "lastId", defaultValue = "AzL8n0Y58m7") String lastId,
		@RequestParam(name = "size", defaultValue = "10") Integer pageSize
	) {
		FindNotificationCommand command = new FindNotificationCommand(principal.getId(), lastId, pageSize);
		List<UserNotificationResponse> notifications = findNotificationsService.doService(command);
		return ResponseEntity.ok(notifications);
	}

	@GetMapping("/new")
	@Operation(summary = "새로운 알림 조회", description = "사용자의 새로운 알림 유무를 조회합니다.", responses = {
			@ApiResponse(responseCode = "200", description = "새로운 알림 유무 조회 성공", content = @Content(mediaType = "text/plain", schema = @Schema(type = "boolean"))),
			@ApiResponse(responseCode = "404", description = "새로운 알림 유무 조회 실패 (사용자 조회 실패)", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
	})
	public ResponseEntity<Boolean> checkUnreadNotificationsExist(@AuthenticationPrincipal UserPrincipal principal) {
		boolean result = checkUnreadNotificationsExistService.doService(principal.getId());
		return ResponseEntity.ok(result);
	}

}
