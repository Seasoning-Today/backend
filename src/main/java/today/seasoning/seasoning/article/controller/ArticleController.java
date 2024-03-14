package today.seasoning.seasoning.article.controller;

import java.util.List;
import javax.validation.Valid;
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
import today.seasoning.seasoning.article.dto.ArticlePreviewResponse;
import today.seasoning.seasoning.article.dto.ArticleResponse;
import today.seasoning.seasoning.article.dto.FindCollageCommand;
import today.seasoning.seasoning.article.dto.FindCollageResponse;
import today.seasoning.seasoning.article.dto.FindFriendArticleResponse;
import today.seasoning.seasoning.article.dto.FindMyArticlesByTermCommand;
import today.seasoning.seasoning.article.dto.FindMyArticlesByYearResponse;
import today.seasoning.seasoning.article.dto.RegisterArticleRequest;
import today.seasoning.seasoning.article.dto.UpdateArticleRequest;
import today.seasoning.seasoning.article.service.ArticleLikeService;
import today.seasoning.seasoning.article.service.DeleteArticleService;
import today.seasoning.seasoning.article.service.FindArticleService;
import today.seasoning.seasoning.article.service.FindCollageService;
import today.seasoning.seasoning.article.service.FindFriendArticlesService;
import today.seasoning.seasoning.article.service.FindMyArticlesByTermService;
import today.seasoning.seasoning.article.service.FindMyArticlesByYearService;
import today.seasoning.seasoning.article.service.RegisterArticleService;
import today.seasoning.seasoning.article.service.UpdateArticleService;
import today.seasoning.seasoning.common.UserPrincipal;
import today.seasoning.seasoning.common.util.TsidUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/article")
public class ArticleController {

    private final RegisterArticleService registerArticleService;
    private final FindArticleService findArticleService;
    private final UpdateArticleService updateArticleService;
    private final DeleteArticleService deleteArticleService;
    private final FindMyArticlesByYearService findMyArticlesByYearService;
    private final FindMyArticlesByTermService findMyArticlesByTermService;
    private final ArticleLikeService articleLikeService;
    private final FindCollageService findCollageService;
    private final FindFriendArticlesService findFriendArticlesService;

    @PostMapping
    public ResponseEntity<String> registerArticle(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestPart(name = "images", required = false) List<MultipartFile> images,
        @RequestPart("request") @Valid RegisterArticleRequest request
    ) {
        Long articleId = registerArticleService.doService(request.buildCommand(principal, images));
        return ResponseEntity.ok(TsidUtil.toString(articleId));
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleResponse> findArticle(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable String articleId
    ) {
        ArticleResponse articleResponse = findArticleService.doFind(principal.getId(), TsidUtil.toLong(articleId));
        return ResponseEntity.ok(articleResponse);
    }

    @PutMapping("/{articleId}")
    public ResponseEntity<Void> updateArticle(@AuthenticationPrincipal UserPrincipal userPrincipal,
        @RequestPart(name = "images", required = false) List<MultipartFile> images,
        @RequestPart("request") @Valid UpdateArticleRequest request,
        @PathVariable String articleId
    ) {
        updateArticleService.doUpdate(request.buildCommand(userPrincipal, articleId, images));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> deleteArticle(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable String articleId
    ) {
        deleteArticleService.doDelete(principal.getId(), TsidUtil.toLong(articleId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list/year/{year}")
    public ResponseEntity<List<FindMyArticlesByYearResponse>> findMyArticlesByYear(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable Integer year
    ) {
        List<FindMyArticlesByYearResponse> response = findMyArticlesByYearService.doFind(principal.getId(), year);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list/term")
    public ResponseEntity<List<ArticlePreviewResponse>> findMyArticlesByTerm(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam(name = "lastId", defaultValue = "AzL8n0Y58m7") String lastArticleId,
        @RequestParam(name = "size", defaultValue = "10") Integer pageSize,
        @RequestParam Integer term
    ) {
        FindMyArticlesByTermCommand command = FindMyArticlesByTermCommand.build(principal, lastArticleId, pageSize, term);
        List<ArticlePreviewResponse> response = findMyArticlesByTermService.doService(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("{articleId}/like")
    public ResponseEntity<Void> likeArticle(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable String articleId
    ) {
        articleLikeService.doLike(principal.getId(), TsidUtil.toLong(articleId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{articleId}/like")
    public ResponseEntity<Void> cancelLikeArticle(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable String articleId
    ) {
        articleLikeService.cancelLike(principal.getId(), TsidUtil.toLong(articleId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/collage")
    public ResponseEntity<List<FindCollageResponse>> findCollage(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam("year") Integer year
    ) {
        FindCollageCommand command = new FindCollageCommand(principal.getId(), year);
        List<FindCollageResponse> response = findCollageService.doFind(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/friends")
    public ResponseEntity<List<FindFriendArticleResponse>> findMyFriendsArticles(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam(name = "lastId", defaultValue = "AzL8n0Y58m7") String lastArticleId,
        @RequestParam(name = "size", defaultValue = "10") Integer pageSize
    ) {
        List<FindFriendArticleResponse> response = findFriendArticlesService.doService(
            principal.getId(),
            TsidUtil.toLong(lastArticleId),
            pageSize);

        return ResponseEntity.ok(response);
    }
}
