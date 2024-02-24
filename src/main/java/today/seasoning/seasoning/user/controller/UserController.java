package today.seasoning.seasoning.user.controller;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import today.seasoning.seasoning.user.service.FindUserSearchableStatusService;
import today.seasoning.seasoning.user.service.UpdateUserProfileService;
import today.seasoning.seasoning.user.service.UpdateUserSearchableStatusService;
import today.seasoning.seasoning.user.service.VerifyAccountIdService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User", description = "사용자 API Document")
public class UserController {

    private final UpdateUserProfileService updateUserProfile;
    private final FindUserProfileService findUserProfileService;
    private final VerifyAccountIdService verifyAccountIdService;
    private final DeleteUserService deleteUserService;
    private final UpdateUserSearchableStatusService updateUserSearchableStatusService;
    private final FindUserSearchableStatusService findUserSearchableStatusService;

    // 프로필 조회
    @GetMapping("/profile")
    @Operation(summary = "프로필 조회", description = "프로필을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "프로필 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileResponse.class)))
    public ResponseEntity<UserProfileResponse> findUserProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserProfileResponse userProfile = findUserProfileService.findUserProfile(userPrincipal.getId());
        return ResponseEntity.ok().body(userProfile);
    }

    // 프로필 수정
    @PutMapping("/profile")
    @Operation(summary = "프로필 수정", description = "프로필을 수정합니다. (multipart/form-data 방식)")
    @ApiResponse(responseCode = "200", description = "프로필 수정 성공", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    public ResponseEntity<Void> updateUserProfile(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestPart(name = "image", required = false) MultipartFile profileImage,
        @RequestPart(name = "request") @Valid UpdateUserProfileRequest request
    ) {
        updateUserProfile.doUpdate(request.buildCommand(principal, profileImage));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-account-id")
    @Operation(summary = "아이디 검증", description = "아이디를 검증합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "아이디 검증 성공", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "409", description = "아이디 검증 실패 (이미 등록된 아이디)", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    public ResponseEntity<Void> checkAccountId(@RequestParam("id") String accountId) {
        if (verifyAccountIdService.verify(new AccountId(accountId))) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @DeleteMapping
    @Operation(summary = "계정 탈퇴", description = "계정을 탈퇴합니다.")
    @ApiResponse(responseCode = "200", description = "계정 탈퇴 성공", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    public ResponseEntity<Void> unregister(@AuthenticationPrincipal UserPrincipal principal) {
        deleteUserService.doService(principal.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/searchable")
    @Operation(summary = "검색 허용 상태 조회", description = "검색 허용 상태를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "검색 허용 상태 조회 성공", content = @Content(mediaType = "text/plain", schema = @Schema(type = "boolean")))
    public ResponseEntity<Boolean> findSearchableStatus(@AuthenticationPrincipal UserPrincipal principal) {
        boolean searchable = findUserSearchableStatusService.doService(principal.getId());
        return ResponseEntity.ok(searchable);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT, params = "searchable")
    @Operation(summary = "검색 허용 상태 변경", description = "검색 허용 상태를 변경합니다.")
    @ApiResponse(responseCode = "200", description = "검색 허용 상태 변경 성공", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    @Parameter(name = "searchable", description = "검색 허용 여부", required = true, example = "true", schema = @Schema(type = "boolean")
    )
    public ResponseEntity<Void> updateSearchableStatus(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam("searchable") boolean searchable
    ) {
        updateUserSearchableStatusService.doService(principal.getId(), searchable);
        return ResponseEntity.ok().build();
    }
}
