package today.seasoning.seasoning.article.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateArticleDto {

	@NotNull
	@JsonProperty("image_modified")
	private Boolean imageModified;

	@NotNull
	private Boolean published;

	@NotNull
	private String contents;
}
