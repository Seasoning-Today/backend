package today.seasoning.seasoning.solarterm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import today.seasoning.seasoning.solarterm.dto.FindSolarTermInfoResponse;
import today.seasoning.seasoning.solarterm.service.RegisterSolarTermsService;
import today.seasoning.seasoning.solarterm.service.SolarTermService;

@RestController
@RequiredArgsConstructor
public class SolarTermController {

    private final SolarTermService solarTermService;
    private final RegisterSolarTermsService registerSolarTermsService;

    @GetMapping("/solarTerm")
    public ResponseEntity<FindSolarTermInfoResponse> findSolarTermInfo() {
        FindSolarTermInfoResponse solarTermInfoResponse = solarTermService.findSolarTermInfo();
        return ResponseEntity.ok(solarTermInfoResponse);
    }

    @PostMapping("/admin/solarTerm")
    public ResponseEntity<Void> registerSolarTerms(@RequestParam("year") int year) {
        registerSolarTermsService.doService(year);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/admin/solarTerm/refresh")
    public ResponseEntity<Void> refreshSolarTerm() {
        solarTermService.updateSolarTerms();
        return ResponseEntity.ok().build();
    }

    @PutMapping("/admin/article_registration_period")
    public ResponseEntity<Void> changeArticleRegistrationPeriod(@RequestParam("value") int period) {
        solarTermService.changeArticleRegistrationPeriod(period);
        return ResponseEntity.ok().build();
    }

}
