package today.seasoning.seasoning.article.dto;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import today.seasoning.seasoning.common.UserPrincipal;


@Getter
@Setter
@NoArgsConstructor
public class RegisterArticleRequest {

	@NotNull
	private Boolean published;

	@NotNull
	private String contents;

	public RegisterArticleCommand buildCommand(UserPrincipal principal, List<MultipartFile> images) {
		return new RegisterArticleCommand(principal.getId(), published, contents, images);
	}
}
