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
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.notification.dto.FindNotificationCommand;
import today.seasoning.seasoning.notification.dto.NotificationDto;
import today.seasoning.seasoning.notification.service.NotificationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
@Tag(name = "Notification", description = "알림 API Document")
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping
	@Operation(summary = "알림 조회", description = "사용자의 알림을 조회합니다.")
	@ApiResponse(
			responseCode = "200", description = "성공적으로 알림을 조회함",
			content = @Content(mediaType = "application/json",
					array = @ArraySchema(schema = @Schema(implementation = NotificationDto.class)))
	)
	@ApiResponse(
			responseCode = "404",
			description = "알림을 조회할 수 없음 (사용자 조회 실패)",
			content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
	)
	@Parameter(
			name = "lastId",
			description = "마지막으로 조회한 알림의 아이디",
			example = "AzL8n0Y58m7",
			in = ParameterIn.QUERY,
			schema = @Schema(type = "string", defaultValue = "AzL8n0Y58m7")
	)
	@Parameter(
			name = "size",
			description = "페이지 크기",
			example = "10",
			in = ParameterIn.QUERY,
			schema = @Schema(type = "integer", defaultValue = "10")
	)
	public ResponseEntity<List<NotificationDto>> findNotifications(
		@AuthenticationPrincipal UserPrincipal principal,
		@RequestParam(name = "lastId", defaultValue = "AzL8n0Y58m7") String lastReadNotificationId,
		@RequestParam(name = "size", defaultValue = "10") Integer pageSize) {

		FindNotificationCommand findNotificationCommand = new FindNotificationCommand(
			principal.getId(),
			TsidUtil.toLong(lastReadNotificationId),
			pageSize);

		List<NotificationDto> notifications = notificationService.findNotifications(
			findNotificationCommand);

		return ResponseEntity.ok(notifications);
	}

}
