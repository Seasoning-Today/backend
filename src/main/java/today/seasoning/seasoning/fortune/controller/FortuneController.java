package today.seasoning.seasoning.fortune.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import today.seasoning.seasoning.common.UserPrincipal;
import today.seasoning.seasoning.fortune.service.FindFortuneService;

@RestController
@RequiredArgsConstructor
public class FortuneController {

    private final FindFortuneService findFortuneService;

    @GetMapping("/today-fortune")
    public ResponseEntity<String> findRandomFortune(@AuthenticationPrincipal UserPrincipal principal) {
        String fortuneContent = findFortuneService.doService(principal.getId());
        return ResponseEntity.ok().body(fortuneContent);
    }
}
