package today.seasoning.seasoning.article.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.article.domain.ArticleRepository;
import today.seasoning.seasoning.article.dto.ArticlePreviewResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindMyArticlesByTermService {

    private final ArticleRepository articleRepository;

    public List<ArticlePreviewResponse> doService(Long userId, int term) {
        return articleRepository.findByUserIdAndTerm(userId, term)
            .stream()
            .map(ArticlePreviewResponse::build)
            .collect(Collectors.toList());
    }
}
