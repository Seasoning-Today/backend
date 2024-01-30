package today.seasoning.seasoning.solarterm.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import today.seasoning.seasoning.solarterm.domain.SolarTerm;

@Getter
@RequiredArgsConstructor
public class SolarTermDto {

    private final int sequence;
    private final LocalDate localDate;

    public static SolarTermDto build(SolarTerm solarTerm) {
        return new SolarTermDto(solarTerm.getSequence(), solarTerm.getDate());
    }
}
