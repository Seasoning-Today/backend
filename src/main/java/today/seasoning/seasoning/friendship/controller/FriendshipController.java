package today.seasoning.seasoning.friendship.controller;

import java.util.List;
import javax.validation.Valid;
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
public class FriendshipController {

    private final UnfriendService unfriendService;
    private final SearchUserService searchUserService;
    private final FindAllFriendsService findAllFriendsService;
    private final SendFriendRequestService sendFriendRequestService;
    private final AcceptFriendRequestService acceptFriendRequestService;
    private final CancelFriendRequestService cancelFriendRequestService;
    private final DeclineFriendRequestService declineFriendRequestService;

    @PostMapping("/add")
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
    public ResponseEntity<List<FindUserFriendsResponse>> findUserFriends(@AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal.getId();
        List<FindUserFriendsResponse> response = findAllFriendsService.doService(userId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/add/accept")
    public ResponseEntity<String> acceptFriendship(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody UserIdDto userIdDto
    ) {
        acceptFriendRequestService.doService(principal.getId(), userIdDto.toLong());
        return ResponseEntity.ok().body("수락 완료");
    }

    @DeleteMapping("/add/cancel")
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
    public ResponseEntity<SearchUserResult> searchFriend(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam("keyword") String friendAccountId
    ) {
        Long userId = principal.getId();
        SearchUserResult result = searchUserService.doService(userId, friendAccountId);
        return ResponseEntity.ok().body(result);
    }
}
