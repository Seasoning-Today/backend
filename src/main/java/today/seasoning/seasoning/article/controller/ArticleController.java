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
import today.seasoning.seasoning.article.dto.UpdateArticleCommand;
import today.seasoning.seasoning.article.dto.UpdateArticleDto;
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

	@GetMapping("/{stringArticleId}")
	public ResponseEntity<FindArticleResult> findArticle(
		@AuthenticationPrincipal UserPrincipal principal,
		@PathVariable String stringArticleId) {

		Long userId = principal.getId();
		Long articleId = TsidUtil.toLong(stringArticleId);

		FindArticleResult findArticleResult = findArticleService.doFind(userId, articleId);

		return ResponseEntity.ok(findArticleResult);
	}

	@PutMapping("/{stringArticleId}")
	public ResponseEntity<Void> updateArticle(@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestPart(name = "images", required = false) List<MultipartFile> images,
		@RequestPart("request") @Valid UpdateArticleDto updateArticleDto,
		@PathVariable String stringArticleId) {

		UpdateArticleCommand command = new UpdateArticleCommand(
			updateArticleDto.getImageModified(),
			userPrincipal.getId(),
			TsidUtil.toLong(stringArticleId),
			updateArticleDto.getPublished(),
			updateArticleDto.getContents(),
			images);

		updateArticleService.doUpdate(command);

		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{stringArticleId}")
	public ResponseEntity<Void> deleteArticle(@AuthenticationPrincipal UserPrincipal principal,
		@PathVariable String stringArticleId) {

		Long userId = principal.getId();
		Long articleId = TsidUtil.toLong(stringArticleId);

		deleteArticleService.doDelete(userId, articleId);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/list/year/{year}")
	public ResponseEntity<List<FindMyArticlesByYearResult>> findMyArticlesByYear(
		@AuthenticationPrincipal UserPrincipal principal,
		@PathVariable Integer year) {

		Long userId = principal.getId();

		List<FindMyArticlesByYearResult> result = findMyArticlesByYearService.doFind(userId, year);

		return ResponseEntity.ok(result);
	}

	@GetMapping("/list/term/{term}")
	public ResponseEntity<List<FindMyArticlesByTermResult>> findMyArticlesByTerm(
		@AuthenticationPrincipal UserPrincipal principal,
		@PathVariable Integer term) {

		Long userId = principal.getId();

		List<FindMyArticlesByTermResult> result = findMyArticlesByTermService.doFind(userId, term);

		return ResponseEntity.ok(result);
	}

	@PostMapping("{articleId}/like")
	public ResponseEntity<Void> likeArticle(@AuthenticationPrincipal UserPrincipal principal,
		@PathVariable("articleId") String stringArticleId) {

		Long userId = principal.getId();
		Long articleId = TsidUtil.toLong(stringArticleId);

		articleLikeService.doLike(userId, articleId);

		return ResponseEntity.ok().build();
	}

	@DeleteMapping("{articleId}/like")
	public ResponseEntity<Void> cancelLikeArticle(@AuthenticationPrincipal UserPrincipal principal,
		@PathVariable("articleId") String stringArticleId) {

		Long userId = principal.getId();
		Long articleId = TsidUtil.toLong(stringArticleId);

		articleLikeService.cancelLike(userId, articleId);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/collage")
	public ResponseEntity<List<FindCollageResult>> findCollage(
		@AuthenticationPrincipal UserPrincipal principal,
		@RequestParam("year") Integer year) {

		Long userId = principal.getId();
		List<FindCollageResult> collage = findCollageService.doFind(userId, year);
		return ResponseEntity.ok(collage);
	}

	@GetMapping("/friends")
	public ResponseEntity<List<FindMyFriendsArticlesResult>> findMyFriendsArticles(
		@AuthenticationPrincipal UserPrincipal principal,
		@RequestParam(name = "lastId", defaultValue = "AzL8n0Y58m7") String stringArticleId,
		@RequestParam(name = "size", defaultValue = "10") Integer pageSize) {

		Long userId = principal.getId();
		Long lastArticleId = TsidUtil.toLong(stringArticleId);

		List<FindMyFriendsArticlesResult> findMyFriendsArticlesResults =
			findMyFriendsArticlesService.doFind(userId, lastArticleId, pageSize);

		return ResponseEntity.ok(findMyFriendsArticlesResults);
	}
}
