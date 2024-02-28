package today.seasoning.seasoning.article.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.article.domain.Article;
import today.seasoning.seasoning.article.domain.ArticleRepository;
import today.seasoning.seasoning.article.dto.ArticlePreviewResponse;
import today.seasoning.seasoning.article.dto.FindMyArticlesByTermCommand;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindMyArticlesByTermService {

    private final ArticleRepository articleRepository;

    public List<ArticlePreviewResponse> doService(FindMyArticlesByTermCommand command) {
        List<Article> articles = command.getTerm() > 0 ?
            articleRepository.findByTerm(command.getUserId(), command.getTerm()) :
            articleRepository.findByPage(command.getUserId(), command.getLastArticleId(), command.getSize());

        return articles.stream().map(ArticlePreviewResponse::build).collect(Collectors.toList());
    }
}
