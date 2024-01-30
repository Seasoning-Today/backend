package today.seasoning.seasoning.solarterm.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import today.seasoning.seasoning.solarterm.dto.FindSolarTermInfoResponse;
import today.seasoning.seasoning.solarterm.service.FindAndRegisterSolarTermsService;
import today.seasoning.seasoning.solarterm.service.SolarTermService;

@RestController
@RequiredArgsConstructor
public class SolarTermController {

    private final SolarTermService solarTermService;
    private final FindAndRegisterSolarTermsService findAndRegisterSolarTermsService;

    @PostMapping("/admin/solar-term")
    public ResponseEntity<Void> registerSolarTermsOfYear(@Valid @RequestBody YearDto yearDto) {
        findAndRegisterSolarTermsService.findAndRegisterSolarTermsOf(yearDto.getYear());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/solarTerm")
    public ResponseEntity<FindSolarTermInfoResponse> findSolarTermInfo() {
        FindSolarTermInfoResponse solarTermInfoResponse = solarTermService.findSolarTermInfo();
        return ResponseEntity.ok(solarTermInfoResponse);
    }

}
