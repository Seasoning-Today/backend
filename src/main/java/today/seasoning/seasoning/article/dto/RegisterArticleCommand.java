package today.seasoning.seasoning.article.dto;

import java.util.List;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class RegisterArticleCommand {

	private final Long userId;
	private final boolean published;
	private final String contents;
	private final List<MultipartFile> images;

	public RegisterArticleCommand(Long userId, boolean published, String contents, List<MultipartFile> images) {
		this.userId = userId;
		this.published = published;
		this.contents = contents;
		this.images = images;
	}
}
