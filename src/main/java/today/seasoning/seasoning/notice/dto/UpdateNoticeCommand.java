package today.seasoning.seasoning.notice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.common.util.TsidUtil;

@Getter
@RequiredArgsConstructor
public class UpdateNoticeCommand {

    private final long id;
    private final String content;

    public UpdateNoticeCommand(String id, String content) {
        this.id = TsidUtil.toLong(id);
        this.content = content;
    }
}
