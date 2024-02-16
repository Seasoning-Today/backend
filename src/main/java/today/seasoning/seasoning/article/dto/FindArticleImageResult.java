package today.seasoning.seasoning.article.dto;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.article.domain.ArticleImage;

@Getter
@RequiredArgsConstructor
public class FindArticleImageResult {

	private final int sequence;
	private final String url;

	public static FindArticleImageResult build(ArticleImage articleImage) {
		return new FindArticleImageResult(articleImage.getSequence(), articleImage.getUrl());
	}

	public static List<FindArticleImageResult> build(List<ArticleImage> articleImages) {
		return articleImages.stream().map(FindArticleImageResult::build).collect(Collectors.toList());
	}
}
