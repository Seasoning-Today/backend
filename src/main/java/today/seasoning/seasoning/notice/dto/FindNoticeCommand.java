package today.seasoning.seasoning.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import today.seasoning.seasoning.common.util.TsidUtil;

@Getter
@AllArgsConstructor
public class FindNoticeCommand {

    private final long lastId;
    private final int pageSize;

    public FindNoticeCommand(String lastId, int pageSize) {
        this.lastId = TsidUtil.toLong(lastId);
        this.pageSize = pageSize;
    }
}
