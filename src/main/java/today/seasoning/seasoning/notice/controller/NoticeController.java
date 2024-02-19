package today.seasoning.seasoning.notice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import today.seasoning.seasoning.notice.service.RegisterNoticeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

    private final RegisterNoticeService registerNoticeService;

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody String content) {
        registerNoticeService.doService(content);
        return ResponseEntity.ok().build();
    }

}
