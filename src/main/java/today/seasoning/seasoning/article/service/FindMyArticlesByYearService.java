package today.seasoning.seasoning.article.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.article.domain.ArticleRepository;
import today.seasoning.seasoning.article.dto.FindMyArticlesByYearResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindMyArticlesByYearService {

    private final ArticleRepository articleRepository;

    public List<FindMyArticlesByYearResponse> doFind(Long userId, int year) {
        return articleRepository.findByUserIdAndYear(userId, year).stream()
            .map(FindMyArticlesByYearResponse::build)
            .collect(Collectors.toList());
    }
}
