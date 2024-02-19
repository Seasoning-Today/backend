package today.seasoning.seasoning.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.notice.domain.Notice;
import today.seasoning.seasoning.notice.domain.NoticeRepository;

@Service
@RequiredArgsConstructor
public class RegisterNoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public void doService(String content) {
        Notice notice = new Notice(content);
        noticeRepository.save(notice);
    }
}
