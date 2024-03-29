package today.seasoning.seasoning.article.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import today.seasoning.seasoning.article.domain.Article;
import today.seasoning.seasoning.article.domain.ArticleRepository;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;

@Component
@RequiredArgsConstructor
public class ValidateArticleLikePolicyImpl implements ValidateArticleLikePolicy {

    private final ArticleRepository articleRepository;
    private final FriendshipRepository friendshipRepository;

    @Override
    public boolean validate(Long userId, Long articleId) {
        Article article = articleRepository.findByIdOrElseThrow(articleId);
        Long authorId = article.getUser().getId();

        // 자신의 글
        if (authorId.equals(userId)) {
            return true;
        }
        // 공개된 친구의 글
        if (article.isPublished() && friendshipRepository.existsByUserIdAndFriendId(userId, authorId)) {
            return true;
        }

        return false;
    }
}
