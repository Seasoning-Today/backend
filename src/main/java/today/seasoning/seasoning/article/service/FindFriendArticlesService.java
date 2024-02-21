package today.seasoning.seasoning.article.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.article.domain.ArticleRepository;
import today.seasoning.seasoning.article.dto.FindMyFriendsArticlesResult;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindMyFriendsArticlesService {

    private final ArticleRepository articleRepository;

    public List<FindMyFriendsArticlesResult> doService(Long userId, Long articleId, Integer pageSize) {
        return articleRepository.findFriendArticles(userId, articleId, pageSize)
            .stream()
            .map(FindMyFriendsArticlesResult::build)
            .collect(Collectors.toList());
    }
}
