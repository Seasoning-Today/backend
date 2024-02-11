package today.seasoning.seasoning.article.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import today.seasoning.seasoning.common.UserPrincipal;
import today.seasoning.seasoning.common.util.TsidUtil;

@Getter
@Setter
@NoArgsConstructor
@Schema(title = "기록장 수정 정보")
public class UpdateArticleRequest {

	@NotNull
	@JsonProperty("image_modified")
	private Boolean imageModified;

	@NotNull
	@Schema(description = "기록장 공개 여부", required = true, example = "True")
	private Boolean published;

	@NotNull
	@Schema(description = "기록장 본문 내용", required = true)
	private String contents;

	public UpdateArticleCommand buildCommand(UserPrincipal principal, String articleId, List<MultipartFile> images) {
		return new UpdateArticleCommand(
			imageModified,
			principal.getId(),
			TsidUtil.toLong(articleId),
			published,
			contents,
			images);
	}
}
