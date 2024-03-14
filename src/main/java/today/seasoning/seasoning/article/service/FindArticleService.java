package today.seasoning.seasoning.article.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.article.domain.Article;
import today.seasoning.seasoning.article.domain.ArticleRepository;
import today.seasoning.seasoning.article.dto.ArticleResponse;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.friendship.service.CheckFriendshipService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindArticleService {

    private final ArticleRepository articleRepository;
    private final CheckFriendshipService checkFriendshipService;

    public ArticleResponse doFind(Long userId, Long articleId) {
        Article article = articleRepository.findByIdOrElseThrow(articleId);
        validatePermission(userId, article);
        return ArticleResponse.build(userId, article);
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
}
