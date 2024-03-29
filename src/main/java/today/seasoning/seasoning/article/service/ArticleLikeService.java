package today.seasoning.seasoning.article.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.article.domain.Article;
import today.seasoning.seasoning.article.domain.ArticleLike;
import today.seasoning.seasoning.article.domain.ArticleLikeRepository;
import today.seasoning.seasoning.article.domain.ArticleRepository;
import today.seasoning.seasoning.article.event.ArticleLikedEvent;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.friendship.service.CheckFriendshipService;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleLikeService {

	private final UserRepository userRepository;
	private final ArticleRepository articleRepository;
	private final ArticleLikeRepository articleLikeRepository;
	private final CheckFriendshipService checkFriendshipService;
	private final ApplicationEventPublisher applicationEventPublisher;

	public void doLike(Long userId, Long articleId) {
		Article article = articleRepository.findByIdOrElseThrow(articleId);
		User user = userRepository.findByIdOrElseThrow(userId);

		validatePermission(userId, article);

		if (articleLikeRepository.findByArticleAndUser(articleId, userId).isEmpty()) {
			User author = article.getUser();
			articleLikeRepository.save(new ArticleLike(article, user));

			if (user != author) {
				ArticleLikedEvent articleLikedEvent = new ArticleLikedEvent(user.getId(), author.getId(), articleId);
				applicationEventPublisher.publishEvent(articleLikedEvent);
			}
		}
	}

	public void cancelLike(Long userId, Long articleId) {
		Article article = articleRepository.findByIdOrElseThrow(articleId);
		validatePermission(userId, article);
		articleLikeRepository.findByArticleAndUser(articleId, userId)
			.ifPresent(articleLike -> articleLikeRepository.deleteById(articleLike.getId()));
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

		throw new CustomException(HttpStatus.FORBIDDEN, "접근 권한 없음");
	}
}
