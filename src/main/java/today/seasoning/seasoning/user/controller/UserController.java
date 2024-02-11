package today.seasoning.seasoning.user.controller;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import today.seasoning.seasoning.common.UserPrincipal;
import today.seasoning.seasoning.user.domain.AccountId;
import today.seasoning.seasoning.user.dto.UpdateUserProfileRequest;
import today.seasoning.seasoning.user.dto.UserProfileDto;
import today.seasoning.seasoning.user.service.DeleteUserService;
import today.seasoning.seasoning.user.service.FindUserProfileService;
import today.seasoning.seasoning.user.service.UpdateUserProfileService;
import today.seasoning.seasoning.user.service.VerifyAccountIdService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User", description = "사용자 API Document")
public class UserController {

    private final UpdateUserProfileService updateUserProfile;
    private final FindUserProfileService findUserProfileService;
    private final VerifyAccountIdService verifyAccountIdService;
    private final DeleteUserService deleteUserService;

    // 프로필 조회
    @GetMapping("/profile")
    @Operation(summary = "프로필 조회", description = "프로필을 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "프로필 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileDto.class))
    )
    public ResponseEntity<UserProfileDto> findUserProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserProfileDto userProfile = findUserProfileService.findUserProfile(userPrincipal.getId());
        return ResponseEntity.ok().body(userProfile);
    }

    // 프로필 수정
    @PutMapping("/profile")
    @Operation(summary = "프로필 수정", description = "프로필을 수정합니다. (multipart/form-data 방식)")
    @ApiResponse(
            responseCode = "200",
            description = "프로필 수정 성공",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
    )
    public ResponseEntity<Void> updateUserProfile(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestPart(name = "image", required = false) MultipartFile profileImage,
        @RequestPart(name = "request") @Valid UpdateUserProfileRequest request
    ) {
        updateUserProfile.doUpdate(request.buildCommand(principal, profileImage));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-account-id")
    @Operation(summary = "아이디 검증", description = "아이디를 검증합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "아이디 검증 성공",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
    )
    @ApiResponse(
            responseCode = "409",
            description = "아이디 검증 실패 (이미 등록된 아이디)",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
    )
    public ResponseEntity<Void> checkAccountId(@RequestParam("id") String accountId) {
        if (verifyAccountIdService.verify(new AccountId(accountId))) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @DeleteMapping
    @Operation(summary = "계정 탈퇴", description = "계정을 탈퇴합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "계정 탈퇴 성공",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
    )
    public ResponseEntity<Void> unregister(@AuthenticationPrincipal UserPrincipal principal) {
        deleteUserService.doService(principal.getId());
        return ResponseEntity.ok().build();
    }
}
