package today.seasoning.seasoning.article.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(title = "기록장 조회 결과")
public class FindArticleResult {

	@Schema(description = "기록장 공개 여부", required = true, example = "True")
	private final boolean published;
	@Schema(description = "연도", required = true, example = "2024")
	private final int year;
	@Schema(description = "절기 순번", required = true, example = "2")
	private final int term;
	@Schema(description = "본문", required = true)
	private final String contents;
	@Schema(description = "이미지 리스트")
	private final List<FindArticleImageResult> images;
	@Schema(description = "좋아요 수", required = true, example = "3")
	private final int likesCount;
	@Schema(description = "좋아요 여부", required = true, example = "True")
	private final boolean userLikes;
}
