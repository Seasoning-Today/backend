package today.seasoning.seasoning.friendship.controller;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import today.seasoning.seasoning.common.UserPrincipal;
import today.seasoning.seasoning.friendship.dto.AccountIdDto;
import today.seasoning.seasoning.friendship.dto.FindUserFriendsResult;
import today.seasoning.seasoning.friendship.dto.SearchUserResult;
import today.seasoning.seasoning.friendship.service.AcceptFriendRequestService;
import today.seasoning.seasoning.friendship.service.CancelFriendRequestService;
import today.seasoning.seasoning.friendship.service.DeclineFriendRequestService;
import today.seasoning.seasoning.friendship.service.FindUserFriendsService;
import today.seasoning.seasoning.friendship.service.RequestFriendshipService;
import today.seasoning.seasoning.friendship.service.SearchUserService;
import today.seasoning.seasoning.friendship.service.UnfriendService;

@RequestMapping("/friend")
@RestController
@RequiredArgsConstructor
public class FriendshipController {

    private final RequestFriendshipService requestFriendshipService;
    private final FindUserFriendsService findUserFriendsService;
    private final AcceptFriendRequestService acceptFriendRequestService;
    private final CancelFriendRequestService cancelFriendRequestService;
    private final DeclineFriendRequestService declineFriendRequestService;
    private final UnfriendService unfriendService;
    private final SearchUserService searchUserService;

    @RequestMapping("/add")
    public ResponseEntity<String> requestFriendship(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody AccountIdDto accountIdDto
    ) {
        Long userId = principal.getId();
        String requesteeAccountId = accountIdDto.getAccountId();

        requestFriendshipService.doService(userId, requesteeAccountId);

        return ResponseEntity.ok().body("신청 완료");
    }

    @GetMapping("/list")
    public ResponseEntity<List<FindUserFriendsResult>> findUserFriends(@AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal.getId();
        List<FindUserFriendsResult> findUserFriendResults = findUserFriendsService.doFind(userId);
        return ResponseEntity.ok().body(findUserFriendResults);
    }

    @PutMapping("/add/accept")
    public ResponseEntity<String> acceptFriendship(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody AccountIdDto accountIdDto
    ) {
        Long userId = principal.getId();
        String requesterAccountId = accountIdDto.getAccountId();

        acceptFriendRequestService.doService(userId, requesterAccountId);

        return ResponseEntity.ok().body("수락 완료");
    }

    @DeleteMapping("/add/cancel")
    public ResponseEntity<String> cancelFriendship(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody AccountIdDto accountIdDto
    ) {
        Long userId = principal.getId();
        String requesteeAccountId = accountIdDto.getAccountId();

        cancelFriendRequestService.doService(userId, requesteeAccountId);

        return ResponseEntity.ok().body("취소 완료");
    }

    @DeleteMapping("/add/decline")
    public ResponseEntity<String> declineFriendship(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody AccountIdDto accountIdDto
    ) {
        Long userId = principal.getId();
        String requesterAccountId = accountIdDto.getAccountId();

        declineFriendRequestService.doService(userId, requesterAccountId);

        return ResponseEntity.ok().body("거절 완료");
    }

    @DeleteMapping("/unfriend")
    public ResponseEntity<String> deleteFriendship(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody AccountIdDto accountIdDto
    ) {
        Long userId = principal.getId();
        String friendAccountId = accountIdDto.getAccountId();

        unfriendService.doService(userId, friendAccountId);
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
