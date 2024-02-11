package today.seasoning.seasoning.article.controller;

import java.util.List;
import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import today.seasoning.seasoning.article.dto.FindArticleResult;
import today.seasoning.seasoning.article.dto.FindCollageResult;
import today.seasoning.seasoning.article.dto.FindMyArticlesByTermResult;
import today.seasoning.seasoning.article.dto.FindMyArticlesByYearResult;
import today.seasoning.seasoning.article.dto.FindMyFriendsArticlesResult;
import today.seasoning.seasoning.article.dto.RegisterArticleRequest;
import today.seasoning.seasoning.article.dto.UpdateArticleRequest;
import today.seasoning.seasoning.article.service.ArticleLikeService;
import today.seasoning.seasoning.article.service.DeleteArticleService;
import today.seasoning.seasoning.article.service.FindArticleService;
import today.seasoning.seasoning.article.service.FindCollageService;
import today.seasoning.seasoning.article.service.FindMyArticlesByTermService;
import today.seasoning.seasoning.article.service.FindMyArticlesByYearService;
import today.seasoning.seasoning.article.service.FindMyFriendsArticlesService;
import today.seasoning.seasoning.article.service.RegisterArticleService;
import today.seasoning.seasoning.article.service.UpdateArticleService;
import today.seasoning.seasoning.common.UserPrincipal;
import today.seasoning.seasoning.common.util.TsidUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/article")
@Tag(name = "Article", description = "기록장 API Document")
public class ArticleController {

    private final RegisterArticleService registerArticleService;
    private final FindArticleService findArticleService;
    private final UpdateArticleService updateArticleService;
    private final DeleteArticleService deleteArticleService;
    private final FindMyArticlesByYearService findMyArticlesByYearService;
    private final FindMyArticlesByTermService findMyArticlesByTermService;
    private final ArticleLikeService articleLikeService;
    private final FindCollageService findCollageService;
    private final FindMyFriendsArticlesService findMyFriendsArticlesService;

    @PostMapping
    @Operation(summary = "기록장 등록", description = "기록장을 등록합니다. (multipart/form-data 방식)")
    @ApiResponse(
            responseCode = "200",
            description = "기록장 등록 성공 (등록한 기록장 id 반환)",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
    )
    @ApiResponse(
            responseCode = "403",
            description = "기록장 열려있는 기간이 아님",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
    )
    @ApiResponse(
            responseCode = "400",
            description = "요청 메시지 오류",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
    )
    @Parameter(
            name = "images",
            description = "기록장 이미지 파일 리스트",
            required = false,
            schema = @Schema(type = "array", implementation = MultipartFile.class)
    )
    public ResponseEntity<String> registerArticle(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestPart(name = "images", required = false) List<MultipartFile> images,
        @RequestPart("request") @Valid RegisterArticleRequest request
    ) {
        Long articleId = registerArticleService.doRegister(request.buildCommand(principal, images));
        return ResponseEntity.ok(TsidUtil.toString(articleId));
    }

    @GetMapping("/{articleId}")
    @Operation(summary = "기록장 조회", description = "기록장을 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "기록장 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = FindArticleResult.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "기록장 조회 실패 (조회 권한 없음)",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
    )
    @ApiResponse(
            responseCode = "404",
            description = "기록장 조회 실패 (기록장 id로 찾을 수 없음)",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
    )
    @Parameter(
            name = "stringArticleId",
            description = "기록장 id",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "string")
    )
    public ResponseEntity<FindArticleResult> findArticle(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable String articleId
    ) {
        FindArticleResult findArticleResult = findArticleService.doFind(principal.getId(), TsidUtil.toLong(articleId));
        return ResponseEntity.ok(findArticleResult);
    }

    @PutMapping("/{articleId}")
    @Operation(summary = "기록장 수정", description = "기록장을 수정합니다. (multipart/form-data 방식)")
    @ApiResponse(
            responseCode = "200",
            description = "기록장 수정 성공",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
    )
    @ApiResponse(
            responseCode = "403",
            description = "기록장 수정 실패 (열려있는 기간이 아니거나 수정 권한 없음)",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
    )
    @ApiResponse(
            responseCode = "404",
            description = "기록장 수정 실패 (기록장 id로 찾을 수 없음)",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
    )
    @ApiResponse(
            responseCode = "400",
            description = "기록장 수정 실패 (이미지 개수 초과)",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
    )
    @Parameter(
            name = "images",
            description = "기록장 이미지 파일 리스트",
            required = false,
            schema = @Schema(type = "array", implementation = MultipartFile.class)
    )
    public ResponseEntity<Void> updateArticle(@AuthenticationPrincipal UserPrincipal userPrincipal,
        @RequestPart(name = "images", required = false) List<MultipartFile> images,
        @RequestPart("request") @Valid UpdateArticleRequest request,
        @PathVariable String articleId
    ) {
        updateArticleService.doUpdate(request.buildCommand(userPrincipal, articleId, images));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{articleId}")
    @Operation(summary = "기록장 삭제", description = "기록장을 삭제합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "기록장 삭제 성공",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
    )
    @ApiResponse(
            responseCode = "403",
            description = "기록장 조회 실패 (조회 권한 없음)",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
    )
    @ApiResponse(
            responseCode = "404",
            description = "기록장 조회 실패 (기록장 id로 찾을 수 없음)",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
    )
    @Parameter(
            name = "articleId",
            description = "기록장 id",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "string")
    )
    public ResponseEntity<Void> deleteArticle(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable String articleId
    ) {
        deleteArticleService.doDelete(principal.getId(), TsidUtil.toLong(articleId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list/year/{year}")
    @Operation(summary = "기록장 연도별 조회", description = "기록장을 연도별로 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "기록장 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = FindMyArticlesByYearResult.class))
            )
    )
    @Parameter(
            name = "year",
            description = "조회할 연도",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "Integer")
    )
    public ResponseEntity<List<FindMyArticlesByYearResult>> findMyArticlesByYear(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable Integer year
    ) {
        List<FindMyArticlesByYearResult> result = findMyArticlesByYearService.doFind(principal.getId(), year);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/list/term/{term}")
    @Operation(summary = "기록장 절기별 조회", description = "기록장을 절기별로 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "기록장 절기별 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = FindMyArticlesByTermResult.class))
            )
    )
    @Parameter(
            name = "term",
            description = "조회할 절기",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "Integer")
    )
    public ResponseEntity<List<FindMyArticlesByTermResult>> findMyArticlesByTerm(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable Integer term
    ) {
        List<FindMyArticlesByTermResult> result = findMyArticlesByTermService.doFind(principal.getId(), term);
        return ResponseEntity.ok(result);
    }

    @PostMapping("{articleId}/like")
    @Operation(summary = "기록장 좋아요", description = "기록장에 좋아요를 누릅니다.")
    @ApiResponse(
            responseCode = "200",
            description = "기록장 좋아요 성공",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
    )
    @ApiResponse(
            responseCode = "409",
            description = "기록장 좋아요 실패 (이미 좋아요를 누름)",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
    )
    public ResponseEntity<Void> likeArticle(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable String articleId
    ) {
        articleLikeService.doLike(principal.getId(), TsidUtil.toLong(articleId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{articleId}/like")
    @Operation(summary = "기록장 좋아요 취소", description = "기록장에 좋아요를 취소합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "기록장 좋아요 취소 성공",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
    )
    @ApiResponse(
            responseCode = "400",
            description = "기록장 좋아요 취소 실패 (누른 좋아요가 없음)",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
    )
    public ResponseEntity<Void> cancelLikeArticle(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable String articleId
    ) {
        articleLikeService.cancelLike(principal.getId(), TsidUtil.toLong(articleId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/collage")
    @Operation(summary = "콜라주 생성", description = "콜라주를 생성합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "콜라주 생성 성공",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = FindCollageResult.class))
            )
    )
    public ResponseEntity<List<FindCollageResult>> findCollage(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam("year") Integer year
    ) {
        List<FindCollageResult> collage = findCollageService.doFind(principal.getId(), year);
        return ResponseEntity.ok(collage);
    }

    @GetMapping("/friends")
    @Operation(summary = "친구 기록장 목록 조회", description = "친구 기록장 목록을 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "친구 기록장 목록 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = FindMyFriendsArticlesResult.class))
            )
    )
    @Parameter(
            name = "lastId",
            description = "첫 조회 시, 생략, 이후 마지막으로 조회한 기록장의 id",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")
    )
    @Parameter(
            name = "size",
            description = "페이지 크기",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "Integer")
    )
    public ResponseEntity<List<FindMyFriendsArticlesResult>> findMyFriendsArticles(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam(name = "lastId", defaultValue = "AzL8n0Y58m7") String lastArticleId,
        @RequestParam(name = "size", defaultValue = "10") Integer pageSize
    ) {
        List<FindMyFriendsArticlesResult> findMyFriendsArticlesResults = findMyFriendsArticlesService.doFind(
            principal.getId(),
            TsidUtil.toLong(lastArticleId),
            pageSize);

        return ResponseEntity.ok(findMyFriendsArticlesResults);
    }
}
