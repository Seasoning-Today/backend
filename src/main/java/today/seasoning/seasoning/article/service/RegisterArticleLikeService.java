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
@RequiredArgsConstructor
public class RegisterArticleLikeService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final ValidateArticleLikePolicy validateArticleLikePolicy;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void doService(Long userId, Long articleId) {
        Article article = articleRepository.findByIdOrElseThrow(articleId);
        User user = userRepository.findByIdOrElseThrow(userId);
        User author = article.getUser();

        // 사용자 권한 검증
        if (!validateArticleLikePolicy.validate(userId, articleId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "권한 없음");
        }

        // 중복 요청의 경우 무시
        if (articleLikeRepository.findByArticleAndUser(articleId, userId).isPresent()) {
            return;
        }

        articleLikeRepository.save(new ArticleLike(article, user));

        // 타인의 글에 좋아요를 누른 경우, 상대방에게 관련 알림 전송
        if (user != author) {
            ArticleLikedEvent articleLikedEvent = new ArticleLikedEvent(user.getId(), author.getId(), articleId);
            applicationEventPublisher.publishEvent(articleLikedEvent);
        }
    }

}
