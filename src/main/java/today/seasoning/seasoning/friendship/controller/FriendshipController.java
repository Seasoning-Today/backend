package today.seasoning.seasoning.friendship.controller;

import java.util.List;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import today.seasoning.seasoning.common.UserPrincipal;
import today.seasoning.seasoning.friendship.dto.FindUserFriendsResponse;
import today.seasoning.seasoning.friendship.dto.SearchUserResult;
import today.seasoning.seasoning.friendship.dto.UserIdDto;
import today.seasoning.seasoning.friendship.service.AcceptFriendRequestService;
import today.seasoning.seasoning.friendship.service.CancelFriendRequestService;
import today.seasoning.seasoning.friendship.service.DeclineFriendRequestService;
import today.seasoning.seasoning.friendship.service.FindAllFriendsService;
import today.seasoning.seasoning.friendship.service.SearchUserService;
import today.seasoning.seasoning.friendship.service.SendFriendRequestService;
import today.seasoning.seasoning.friendship.service.UnfriendService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friend")
@Tag(name = "Friend", description = "친구 API Document")
public class FriendshipController {

    private final UnfriendService unfriendService;
    private final SearchUserService searchUserService;
    private final FindAllFriendsService findAllFriendsService;
    private final SendFriendRequestService sendFriendRequestService;
    private final AcceptFriendRequestService acceptFriendRequestService;
    private final CancelFriendRequestService cancelFriendRequestService;
    private final DeclineFriendRequestService declineFriendRequestService;

    @PostMapping("/add")
    @Operation(summary = "친구 신청", description = "다른 사용자에게 친구 요청을 보냅니다.", method = "POST", responses = {
            @ApiResponse(responseCode = "200", description = "친구 신청 성공", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "409", description = "친구 신청 실패 (이미 친구이거나, 친구 신청을 받았거나, 친구 신청을 보낸 상태)", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "친구 신청 실패 (사용자를 찾을 수 없음)", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    public ResponseEntity<String> requestFriendship(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody UserIdDto userIdDto
    ) {
        Long userId = principal.getId();
        Long requesteeUserId = userIdDto.toLong();

        sendFriendRequestService.doService(userId, requesteeUserId);

        return ResponseEntity.ok().body("신청 완료");
    }

    @GetMapping("/list")
    @Operation(summary = "친구 목록 조회", description = "특정 사용자의 친구 목록을 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "성공적으로 특정 사용자의 친구 목록을 가져옴", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FindUserFriendsResponse.class))))
    })
    public ResponseEntity<List<FindUserFriendsResponse>> findUserFriends(@AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal.getId();
        List<FindUserFriendsResponse> response = findAllFriendsService.doService(userId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/add/accept")
    @Operation(summary = "친구 신청 수락", description = "친구 신청을 수락합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "친구 신청 수락 성공", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "친구 신청 수락 실패", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    public ResponseEntity<String> acceptFriendship(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody UserIdDto userIdDto
    ) {
        acceptFriendRequestService.doService(principal.getId(), userIdDto.toLong());
        return ResponseEntity.ok().body("수락 완료");
    }

    @DeleteMapping("/add/cancel")
    @Operation(summary = "친구 신청 취소", description = "친구 신청을 취소합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "친구 신청 취소 성공", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "친구 신청 취소 실패 (사용자를 찾을 수 없음)", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    public ResponseEntity<String> cancelFriendship(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody UserIdDto userIdDto
    ) {
        Long userId = principal.getId();
        Long requesteeUserId = userIdDto.toLong();

        cancelFriendRequestService.doService(userId, requesteeUserId);

        return ResponseEntity.ok().body("취소 완료");
    }

    @DeleteMapping("/add/decline")
    @Operation(summary = "친구 신청 거절", description = "친구 신청을 거절합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "친구 신청 거절 성공", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "친구 신청 거절 실패 (사용자를 찾을 수 없음)", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    public ResponseEntity<String> declineFriendship(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody UserIdDto userIdDto
    ) {
        Long userId = principal.getId();
        Long requesterUserId = userIdDto.toLong();

        declineFriendRequestService.doService(userId, requesterUserId);

        return ResponseEntity.ok().body("거절 완료");
    }

    @DeleteMapping("/unfriend")
    @Operation(summary = "친구 삭제", description = "친구를 삭제합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "친구 삭제 성공", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "친구 삭제 실패 (사용자를 찾을 수 없음)", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    public ResponseEntity<String> deleteFriendship(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody UserIdDto userIdDto
    ) {
        Long userId = principal.getId();
        Long friendUserId = userIdDto.toLong();

        unfriendService.doService(userId, friendUserId);
        return ResponseEntity.ok().body("삭제 완료");
    }

    @GetMapping("/search")
    @Operation(summary = "친구 검색", description = "사용자를 검색합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "성공적으로 친구를 검색함", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchUserResult.class))),
            @ApiResponse(responseCode = "400", description = "사용자를 찾을 수 없음", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @Parameter(name = "keyword", description = "검색할 사용자의 아이디", required = true, example = "linguu", schema = @Schema(type = "string")
    )
    public ResponseEntity<SearchUserResult> searchFriend(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam("keyword") String friendAccountId
    ) {
        Long userId = principal.getId();
        SearchUserResult result = searchUserService.doService(userId, friendAccountId);
        return ResponseEntity.ok().body(result);
    }
}
