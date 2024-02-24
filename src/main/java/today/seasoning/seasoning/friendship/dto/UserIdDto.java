package today.seasoning.seasoning.friendship.dto;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import today.seasoning.seasoning.common.util.TsidUtil;

@Getter
@Setter
@NoArgsConstructor
@Schema(title = "사용자 아이디")
public class UserIdDto {

	@NotBlank
	private String id;

	public Long toLong() {
		return TsidUtil.toLong(id);
	}
}
