package today.seasoning.seasoning.article.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.common.UserPrincipal;
import today.seasoning.seasoning.common.util.TsidUtil;

@Getter
@RequiredArgsConstructor
public class FindMyArticlesByTermCommand {

    private final Long userId;
    private final Long lastArticleId;
    private final int size;
    private final int term;

    public static FindMyArticlesByTermCommand build(UserPrincipal userPrincipal, String lastArticleId, Integer pageSize, Integer term) {
        return new FindMyArticlesByTermCommand(userPrincipal.getId(), TsidUtil.toLong(lastArticleId), pageSize, term);
    }
}
