package today.seasoning.seasoning.article.dto;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.article.domain.ArticleImage;

@Getter
@RequiredArgsConstructor
public class ArticleImageResponse {

	private final int sequence;
	private final String url;

	public static ArticleImageResponse build(ArticleImage articleImage) {
		return new ArticleImageResponse(articleImage.getSequence(), articleImage.getUrl());
	}

	public static List<ArticleImageResponse> build(List<ArticleImage> articleImages) {
		return articleImages.stream().map(ArticleImageResponse::build).collect(Collectors.toList());
	}
}
