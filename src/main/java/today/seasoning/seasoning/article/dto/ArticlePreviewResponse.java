package today.seasoning.seasoning.article.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import today.seasoning.seasoning.article.domain.Article;
import today.seasoning.seasoning.article.domain.ArticleImage;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.common.util.TsidUtil;

@Getter
@RequiredArgsConstructor
public class ArticlePreviewResponse {

	private final String id;
	private final int year;
	private final int term;
	private final String preview;
	private final String image;

	public static ArticlePreviewResponse build(Article article) {
		return new ArticlePreviewResponse(
			TsidUtil.toString(article.getId()),
			article.getCreatedYear(),
			article.getCreatedTerm(),
			getContentsPreview(article.getContents()),
			getFirstImageUrl(article.getArticleImages()));
	}

	private static String getContentsPreview(String contents) {
		if (!StringUtils.hasLength(contents)) {
			return "";
		}

		try {
			StringBuilder preview = new StringBuilder();
			ArticleContent[] articleContents = new ObjectMapper().readValue(contents, ArticleContent[].class);

			for (ArticleContent articleContent : articleContents) {
				preview.append(articleContent.text.replace("\n", "")).append(" ");
			}

			return preview.substring(0, Math.min(preview.length() - 1, 100));
		} catch (Exception e) {
			throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	private static String getFirstImageUrl(List<ArticleImage> images) {
		if (images == null || images.isEmpty()) {
			return null;
		}

		return images.stream()
			.min(Comparator.comparingInt(ArticleImage::getSequence))
			.map(ArticleImage::getUrl)
			.orElse(null);
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class ArticleContent {

		private String type;
		private String text;
	}

}
