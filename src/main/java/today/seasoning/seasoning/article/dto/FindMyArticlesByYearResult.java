package today.seasoning.seasoning.article.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(title = "기록장 연도별 조회 응답")
public class FindMyArticlesByYearResult {

	@Schema(description = "기록장 id")
	private final String id;
	@Schema(description = "기록장 절기")
	private final int term;

	public FindMyArticlesByYearResult(String id, int term) {
		this.id = id;
		this.term = term;
	}
}
