package today.seasoning.seasoning.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
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
@Schema(title = "사용자 프로필 수정 요청")
public class UpdateUserProfileRequest {

	@NotNull
	@JsonProperty("image_modified")
	private Boolean imageModified;

	@NotBlank
	private String accountId;

	@NotBlank
	private String nickname;

	public UpdateUserProfileCommand buildCommand(UserPrincipal principal, MultipartFile image) {
		return new UpdateUserProfileCommand(imageModified, principal.getId(), accountId, nickname, image);
	}
}
