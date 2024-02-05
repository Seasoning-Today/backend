package today.seasoning.seasoning.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserProfileDto {

	@NotNull
	@JsonProperty("image_modified")
	private Boolean imageModified;

	@NotBlank
	private String accountId;

	@NotBlank
	private String nickname;
}
