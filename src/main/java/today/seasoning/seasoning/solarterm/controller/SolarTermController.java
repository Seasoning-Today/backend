package today.seasoning.seasoning.solarterm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/admin/solarTerm")
    public ResponseEntity<Void> registerSolarTerms(@RequestParam("year") int year) {
        registerSolarTermsService.doService(year);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/solarTerm")
    public ResponseEntity<FindSolarTermInfoResponse> findSolarTermInfo() {
        FindSolarTermInfoResponse solarTermInfoResponse = solarTermService.findSolarTermInfo();
        return ResponseEntity.ok(solarTermInfoResponse);
    }

}
