package today.seasoning.seasoning.article.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.article.domain.ArticleLikeRepository;
import today.seasoning.seasoning.common.exception.CustomException;

@Service
@RequiredArgsConstructor
public class CancelArticleLikeService {

    private final ArticleLikeRepository articleLikeRepository;
    private final ValidateArticleLikePolicy validateArticleLikePolicy;

    @Transactional
    public void doService(Long userId, Long articleId) {
        if (!validateArticleLikePolicy.validate(userId, articleId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "권한 없음");
        }
        articleLikeRepository.findByArticleAndUser(articleId, userId).ifPresent(articleLikeRepository::delete);
    }
}
