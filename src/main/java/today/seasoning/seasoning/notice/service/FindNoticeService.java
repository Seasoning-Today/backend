package today.seasoning.seasoning.notice.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import today.seasoning.seasoning.notice.domain.NoticeRepository;
import today.seasoning.seasoning.notice.dto.FindNoticeCommand;
import today.seasoning.seasoning.notice.dto.NoticeResponse;

@Service
@RequiredArgsConstructor
public class FindNoticeService {

    private final NoticeRepository noticeRepository;

    public List<NoticeResponse> doService(FindNoticeCommand command) {
        return noticeRepository.find(command.getLastId(), command.getPageSize())
            .stream()
            .map(NoticeResponse::build)
            .collect(Collectors.toList());
    }
}
