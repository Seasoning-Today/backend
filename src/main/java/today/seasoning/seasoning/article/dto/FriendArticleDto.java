package today.seasoning.seasoning.article.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(title = "친구 기록장")
public class FriendArticleDto {

	@Schema(description = "기록장 id", required = true)
	private final String id;
	@Schema(description = "기록장 연도", required = true)
	private final int year;
	@Schema(description = "기록장 절기", required = true)
	private final int term;
	@Schema(description = "미리보기", required = true)
	private final String preview;
	@Schema(description = "기록장 이미지", required = true)
	private final String image;

	public FriendArticleDto(String id, int year, int term, String preview, String image) {
		this.id = id;
		this.year = year;
		this.term = term;
		this.preview = preview;
		this.image = image;
	}
}
