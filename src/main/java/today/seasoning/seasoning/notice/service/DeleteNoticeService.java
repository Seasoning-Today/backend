package today.seasoning.seasoning.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.notice.domain.Notice;
import today.seasoning.seasoning.notice.domain.NoticeRepository;
import today.seasoning.seasoning.notice.dto.DeleteNoticeCommand;

@Service
@RequiredArgsConstructor
public class DeleteNoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public void doService(DeleteNoticeCommand command) {
        Notice notice = noticeRepository.findByIdOrElseThrow(command.getId());
        noticeRepository.delete(notice);
    }
}
