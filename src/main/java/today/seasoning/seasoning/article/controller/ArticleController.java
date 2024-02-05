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
    public ResponseEntity<String> registerArticle(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestPart(name = "images", required = false) List<MultipartFile> images,
        @RequestPart("request") @Valid RegisterArticleRequest request
    ) {
        Long articleId = registerArticleService.doRegister(request.buildCommand(principal, images));
        return ResponseEntity.ok(TsidUtil.toString(articleId));
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<FindArticleResult> findArticle(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable String articleId
    ) {
        FindArticleResult findArticleResult = findArticleService.doFind(principal.getId(), TsidUtil.toLong(articleId));
        return ResponseEntity.ok(findArticleResult);
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
    public ResponseEntity<List<FindMyArticlesByYearResult>> findMyArticlesByYear(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable Integer year
    ) {
        List<FindMyArticlesByYearResult> result = findMyArticlesByYearService.doFind(principal.getId(), year);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/list/term/{term}")
    public ResponseEntity<List<FindMyArticlesByTermResult>> findMyArticlesByTerm(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable Integer term
    ) {
        List<FindMyArticlesByTermResult> result = findMyArticlesByTermService.doFind(principal.getId(), term);
        return ResponseEntity.ok(result);
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
    public ResponseEntity<List<FindCollageResult>> findCollage(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam("year") Integer year
    ) {
        List<FindCollageResult> collage = findCollageService.doFind(principal.getId(), year);
        return ResponseEntity.ok(collage);
    }

    @GetMapping("/friends")
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
