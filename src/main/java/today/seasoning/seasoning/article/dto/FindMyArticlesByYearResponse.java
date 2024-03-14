package today.seasoning.seasoning.article.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.article.domain.Article;
import today.seasoning.seasoning.common.util.TsidUtil;

@Getter
@RequiredArgsConstructor
public class FindMyArticlesByYearResponse {

    private final String id;
    private final int term;

    public static FindMyArticlesByYearResponse build(Article article) {
        return new FindMyArticlesByYearResponse(TsidUtil.toString(article.getId()), article.getCreatedTerm());
    }
}
