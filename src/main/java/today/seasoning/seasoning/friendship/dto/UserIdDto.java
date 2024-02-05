package today.seasoning.seasoning.friendship.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import today.seasoning.seasoning.common.util.TsidUtil;

@Getter
@Setter
@NoArgsConstructor
public class UserIdDto {

	@NotBlank
	private String id;

	public Long toLong() {
		return TsidUtil.toLong(id);
	}
}
