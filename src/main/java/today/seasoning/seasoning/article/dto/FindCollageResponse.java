package today.seasoning.seasoning.article.dto;

import java.util.Comparator;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.article.domain.Article;
import today.seasoning.seasoning.article.domain.ArticleImage;
import today.seasoning.seasoning.common.util.TsidUtil;

@Getter
@RequiredArgsConstructor
public class FindCollageResponse {

    private final int term;
    private final String articleId;
    private final String image;

    public static FindCollageResponse build(Article article) {
        List<ArticleImage> articleImages = article.getArticleImages();

        return new FindCollageResponse(article.getCreatedTerm(),
            TsidUtil.toString(article.getId()),
            getFirstImageUrl(articleImages));
    }

    private static String getFirstImageUrl(List<ArticleImage> images) {
        return images.stream()
            .min(Comparator.comparingInt(ArticleImage::getSequence))
            .map(ArticleImage::getUrl)
            .orElse(null);
    }
}
