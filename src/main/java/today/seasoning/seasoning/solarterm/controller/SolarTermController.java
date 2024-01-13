package today.seasoning.seasoning.solarterm.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import today.seasoning.seasoning.solarterm.service.FindAndRegisterSolarTermsService;

@RestController
@RequiredArgsConstructor
public class SolarTermController {

    private final FindAndRegisterSolarTermsService findAndRegisterSolarTermsService;

    @PostMapping("/admin/solar-term")
    public ResponseEntity<Void> registerSolarTermsOfYear(@Valid @RequestBody YearDto yearDto) {
        findAndRegisterSolarTermsService.findAndRegisterSolarTermsOf(yearDto.getYear());
        return ResponseEntity.ok().build();
    }

}
