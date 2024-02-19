package today.seasoning.seasoning.notice.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.notice.domain.Notice;

@Getter
@RequiredArgsConstructor
public class NoticeResponse {

    private final LocalDateTime date;
    private final String content;

    public static NoticeResponse build(Notice notice) {
        return new NoticeResponse(notice.getCreatedDate(), notice.getContent());
    }
}
