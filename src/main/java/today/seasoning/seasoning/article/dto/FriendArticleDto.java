package today.seasoning.seasoning.article.dto;

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
public class FriendArticleDto {

	private final String id;
	private final int year;
	private final int term;
	private final String preview;
	private final String image;

	public static FriendArticleDto build(Article article) {
		return new FriendArticleDto(
			TsidUtil.toString(article.getId()),
			article.getCreatedYear(),
			article.getCreatedTerm(),
			getPreview(article.getContents()),
			getImage(article.getArticleImages()));
	}

	private static String getPreview(String contents) {
		if (!StringUtils.hasLength(contents)) {
			return "";
		}

		try {
			ContentUnit[] contentUnits = new ObjectMapper().readValue(contents, ContentUnit[].class);
			StringBuilder preview = new StringBuilder();

			for (ContentUnit contentUnit : contentUnits) {
				preview.append(contentUnit.text.replace("\n", "")).append(" ");
			}

			return preview.substring(0, Math.min(preview.length() - 1, 100));
		} catch (Exception e) {
			throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	private static String getImage(List<ArticleImage> images) {
		return images.stream()
			.min(Comparator.comparingInt(ArticleImage::getSequence))
			.map(ArticleImage::getUrl)
			.orElse(null);
	}

	@Getter
	@Setter
	private static class ContentUnit {

		private String type;
		private String text;
	}

}
