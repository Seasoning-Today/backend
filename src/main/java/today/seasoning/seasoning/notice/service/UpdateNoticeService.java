package today.seasoning.seasoning.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.notice.domain.Notice;
import today.seasoning.seasoning.notice.domain.NoticeRepository;
import today.seasoning.seasoning.notice.dto.UpdateNoticeCommand;

@Service
@RequiredArgsConstructor
public class UpdateNoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public void doService(UpdateNoticeCommand command) {
        Notice notice = noticeRepository.findByIdOrElseThrow(command.getId());
        notice.updateContent(command.getContent());
    }
}
