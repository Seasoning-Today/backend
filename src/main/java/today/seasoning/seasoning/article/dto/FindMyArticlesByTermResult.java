package today.seasoning.seasoning.article.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(title = "기록장 절기별 조회 결과")
public class FindMyArticlesByTermResult {

	@Schema(description = "기록장 id", required = true)
	private final String id;
	@Schema(description = "기록장 연도", required = true)
	private final int year;
	@Schema(description = "기록장 미리보기", required = true)
	private final String preview;
	@Schema(description = "기록장 이미지", required = true)
	private final String image;


	public FindMyArticlesByTermResult(String id, int year, String preview, String image) {
		this.id = id;
		this.year = year;
		this.preview = preview;
		this.image = image;
	}
}
