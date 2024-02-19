package today.seasoning.seasoning.user.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import today.seasoning.seasoning.common.UserPrincipal;
import today.seasoning.seasoning.user.domain.AccountId;
import today.seasoning.seasoning.user.dto.UpdateUserProfileRequest;
import today.seasoning.seasoning.user.dto.UserProfileResponse;
import today.seasoning.seasoning.user.service.DeleteUserService;
import today.seasoning.seasoning.user.service.FindUserProfileService;
import today.seasoning.seasoning.user.service.UpdateUserProfileService;
import today.seasoning.seasoning.user.service.UpdateUserSearchableStatusService;
import today.seasoning.seasoning.user.service.VerifyAccountIdService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UpdateUserProfileService updateUserProfile;
    private final FindUserProfileService findUserProfileService;
    private final VerifyAccountIdService verifyAccountIdService;
    private final DeleteUserService deleteUserService;
    private final UpdateUserSearchableStatusService updateUserSearchableStatusService;

    // 프로필 조회
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> findUserProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserProfileResponse userProfile = findUserProfileService.findUserProfile(userPrincipal.getId());
        return ResponseEntity.ok().body(userProfile);
    }

    // 프로필 수정
    @PutMapping("/profile")
    public ResponseEntity<Void> updateUserProfile(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestPart(name = "image", required = false) MultipartFile profileImage,
        @RequestPart(name = "request") @Valid UpdateUserProfileRequest request
    ) {
        updateUserProfile.doUpdate(request.buildCommand(principal, profileImage));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-account-id")
    public ResponseEntity<Void> checkAccountId(@RequestParam("id") String accountId) {
        if (verifyAccountIdService.verify(new AccountId(accountId))) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> unregister(@AuthenticationPrincipal UserPrincipal principal) {
        deleteUserService.doService(principal.getId());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "", method = RequestMethod.PUT, params = "searchable")
    public ResponseEntity<Void> updateSearchableStatus(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam("searchable") boolean searchable
    ) {
        updateUserSearchableStatusService.doService(principal.getId(), searchable);
        return ResponseEntity.ok().build();
    }
}
