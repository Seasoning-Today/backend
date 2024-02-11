package today.seasoning.seasoning.article.dto;

import java.util.List;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import today.seasoning.seasoning.common.UserPrincipal;


@Getter
@Setter
@NoArgsConstructor
@Schema(title = "기록장 등록 정보")
public class RegisterArticleRequest {

	@NotNull
	@Schema(description = "기록장 공개 여부", required = true, example = "True")
	private Boolean published;

	@NotNull
	@Schema(description = "기록장 본문 내용", required = true)
	private String contents;

	public RegisterArticleCommand buildCommand(UserPrincipal principal, List<MultipartFile> images) {
		return new RegisterArticleCommand(principal.getId(), published, contents, images);
	}
}
