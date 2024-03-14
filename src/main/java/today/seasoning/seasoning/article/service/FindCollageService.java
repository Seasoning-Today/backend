package today.seasoning.seasoning.article.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.article.domain.ArticleRepository;
import today.seasoning.seasoning.article.dto.FindCollageCommand;
import today.seasoning.seasoning.article.dto.FindCollageResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindCollageService {

    private final ArticleRepository articleRepository;

    public List<FindCollageResponse> doFind(FindCollageCommand command) {
        return articleRepository.findByUserIdAndYear(command.getUserId(), command.getYear())
            .stream()
            .map(FindCollageResponse::build)
            .collect(Collectors.toList());
    }
}
