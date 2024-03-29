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
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleLikeService {

	private final UserRepository userRepository;
	private final ArticleRepository articleRepository;
	private final ArticleLikeRepository articleLikeRepository;
	private final ValidateArticleLikePolicy validateArticleLikePolicy;
	private final ApplicationEventPublisher applicationEventPublisher;

	public void doLike(Long userId, Long articleId) {
		Article article = articleRepository.findByIdOrElseThrow(articleId);
		User user = userRepository.findByIdOrElseThrow(userId);

		if (!validateArticleLikePolicy.validate(userId, articleId)) {
			throw new CustomException(HttpStatus.FORBIDDEN, "접근 권한 없음");
		}

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
		if (!validateArticleLikePolicy.validate(userId, articleId)) {
			throw new CustomException(HttpStatus.FORBIDDEN, "접근 권한 없음");
		}

		articleLikeRepository.findByArticleAndUser(articleId, userId).ifPresent(articleLikeRepository::delete);
	}
}
