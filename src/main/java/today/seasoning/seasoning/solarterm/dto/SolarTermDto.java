package today.seasoning.seasoning.solarterm.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.solarterm.domain.SolarTerm;

@Getter
@RequiredArgsConstructor
public class SolarTermDto {

    @Schema(description = "절기 순번", example = "1")
    private final int sequence;
    @Schema(description = "기록 가능한 마지막 날짜")
    private final LocalDate date;

    public static SolarTermDto build(SolarTerm solarTerm) {
        return new SolarTermDto(solarTerm.getSequence(), solarTerm.getDate());
    }
}
