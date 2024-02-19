package today.seasoning.seasoning.notice.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import today.seasoning.seasoning.notice.dto.DeleteNoticeCommand;
import today.seasoning.seasoning.notice.dto.FindNoticeCommand;
import today.seasoning.seasoning.notice.dto.NoticeResponse;
import today.seasoning.seasoning.notice.dto.UpdateNoticeCommand;
import today.seasoning.seasoning.notice.service.DeleteNoticeService;
import today.seasoning.seasoning.notice.service.FindNoticeService;
import today.seasoning.seasoning.notice.service.RegisterNoticeService;
import today.seasoning.seasoning.notice.service.UpdateNoticeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

    private final FindNoticeService findNoticeService;
    private final UpdateNoticeService updateNoticeService;
    private final DeleteNoticeService deleteNoticeService;
    private final RegisterNoticeService registerNoticeService;

    @GetMapping
    public ResponseEntity<List<NoticeResponse>> find(
        @RequestParam(name = "lastId", defaultValue = "AzL8n0Y58m7") String lastNoticeId,
        @RequestParam(name = "size", defaultValue = "10") Integer pageSize
    ) {
        FindNoticeCommand command = new FindNoticeCommand(lastNoticeId, pageSize);
        List<NoticeResponse> noticeResponses = findNoticeService.doService(command);
        return ResponseEntity.ok(noticeResponses);
    }

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody String content) {
        registerNoticeService.doService(content);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestParam("id") String noticeId, @RequestBody String content) {
        UpdateNoticeCommand command = new UpdateNoticeCommand(noticeId, content);
        updateNoticeService.doService(command);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam("id") String noticeId) {
        DeleteNoticeCommand command = new DeleteNoticeCommand(noticeId);
        deleteNoticeService.doService(command);
        return ResponseEntity.ok().build();
    }
}
