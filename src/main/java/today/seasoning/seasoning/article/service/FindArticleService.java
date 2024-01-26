package today.seasoning.seasoning.article.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.article.domain.Article;
import today.seasoning.seasoning.article.domain.ArticleLike;
import today.seasoning.seasoning.article.domain.ArticleRepository;
import today.seasoning.seasoning.article.dto.FindArticleImageResult;
import today.seasoning.seasoning.article.dto.FindArticleResult;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.friendship.service.CheckFriendshipService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindArticleService {

	private final ArticleRepository articleRepository;
	private final CheckFriendshipService checkFriendshipService;

	public FindArticleResult doFind(Long userId, Long articleId) {
		Article article = articleRepository.findById(articleId)
			.orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "기록장 조회 실패"));

		validatePermission(userId, article);

		return createFindResult(userId, article);
	}

	private void validatePermission(Long userId, Article article) {
		Long authorId = article.getUser().getId();

		// 자신의 글
		if (authorId.equals(userId)) {
			return;
		}
		// 공개된 친구의 글
		if (article.isPublished() && checkFriendshipService.doCheck(userId, authorId)) {
			return;
		}

		throw new CustomException(HttpStatus.FORBIDDEN, "조회 권한 없음");
	}

	private FindArticleResult createFindResult(Long userId, Article article) {
		List<FindArticleImageResult> findArticleImageResults
			= createFindArticleImageResults(article);

		boolean userLikesArticle = checkUserLikesArticle(userId, article);

		return new FindArticleResult(
			article.isPublished(),
			article.getCreatedYear(),
			article.getCreatedTerm(),
			article.getContents(),
			findArticleImageResults,
			article.getArticleLikes().size(),
			userLikesArticle);
	}

	private List<FindArticleImageResult> createFindArticleImageResults(Article article) {
		return article.getArticleImages()
			.stream()
			.map(FindArticleImageResult::build)
			.collect(Collectors.toList());
	}

	private boolean checkUserLikesArticle(Long userId, Article article) {
		return article.getArticleLikes()
			.stream()
			.map(ArticleLike::getUser)
			.anyMatch(user -> user.getId().equals(userId));
	}

}
