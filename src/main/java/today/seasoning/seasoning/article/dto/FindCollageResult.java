package today.seasoning.seasoning.article.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(title = "콜라주 조회 응답")
public class FindCollageResult {

	@Schema(description = "절기")
	private final int term;
	@Schema(description = "기록장 id")
	private final String articleId;
	@Schema(description = "이미지")
	private final String image;

	public FindCollageResult(int term, String articleId, String image) {
		this.term = term;
		this.articleId = articleId;
		this.image = image;
	}
}
