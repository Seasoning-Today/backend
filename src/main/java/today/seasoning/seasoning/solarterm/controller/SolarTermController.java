package today.seasoning.seasoning.solarterm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "SolarTerm", description = "절기 조회 API Document")
public class SolarTermController {

    private final SolarTermService solarTermService;
    private final RegisterSolarTermsService registerSolarTermsService;

    @PostMapping("/admin/solarTerm")
    public ResponseEntity<Void> registerSolarTerms(@RequestParam("year") int year) {
        registerSolarTermsService.doService(year);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/solarTerm")
    @Operation(summary = "절기 조회", description = "절기 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "절기 정보 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FindSolarTermInfoResponse.class)))
    public ResponseEntity<FindSolarTermInfoResponse> findSolarTermInfo() {
        FindSolarTermInfoResponse solarTermInfoResponse = solarTermService.findSolarTermInfo();
        return ResponseEntity.ok(solarTermInfoResponse);
    }

}
