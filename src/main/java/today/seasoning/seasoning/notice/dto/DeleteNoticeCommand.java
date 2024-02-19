package today.seasoning.seasoning.notice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.common.util.TsidUtil;

@Getter
@RequiredArgsConstructor
public class DeleteNoticeCommand {

    private final long id;

    public DeleteNoticeCommand(String id) {
        this.id = TsidUtil.toLong(id);
    }
}
