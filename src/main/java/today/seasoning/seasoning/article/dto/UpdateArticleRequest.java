package today.seasoning.seasoning.article.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import today.seasoning.seasoning.common.UserPrincipal;
import today.seasoning.seasoning.common.util.TsidUtil;

@Getter
@Setter
@NoArgsConstructor
public class UpdateArticleRequest {

	@NotNull
	@JsonProperty("image_modified")
	private Boolean imageModified;

	@NotNull
	private Boolean published;

	@NotNull
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
