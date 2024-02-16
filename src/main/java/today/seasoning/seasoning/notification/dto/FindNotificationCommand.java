package today.seasoning.seasoning.notification.dto;

import lombok.Getter;
import today.seasoning.seasoning.common.util.TsidUtil;

@Getter
public class FindNotificationCommand {

	private final Long userId;
	private final Long lastId;
	private final int pageSize;

	public FindNotificationCommand(Long userId, String lastId, int pageSize) {
		this.userId = userId;
		this.lastId = TsidUtil.toLong(lastId);
		this.pageSize = pageSize;
	}
}
