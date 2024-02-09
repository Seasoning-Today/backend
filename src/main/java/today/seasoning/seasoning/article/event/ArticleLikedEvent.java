package today.seasoning.seasoning.article.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ArticleLikedEvent {

    private final Long userId;
    private final Long authorId;
    private final Long articleId;

}
