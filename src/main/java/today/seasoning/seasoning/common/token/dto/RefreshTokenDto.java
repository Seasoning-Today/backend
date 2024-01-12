package today.seasoning.seasoning.common.token.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RefreshTokenDto {

    @NotBlank
    private String refreshToken;

}
