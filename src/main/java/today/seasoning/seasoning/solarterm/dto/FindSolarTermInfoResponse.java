package today.seasoning.seasoning.solarterm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Getter;
import today.seasoning.seasoning.solarterm.domain.SolarTerm;

@Getter
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class FindSolarTermInfoResponse {

    private final boolean recordable;
    private final SolarTermDto currentTerm;
    private final SolarTermDto nextTerm;
    private final SolarTermDto recordTerm;

    public static FindSolarTermInfoResponse build(SolarTerm currentSolarTerm, SolarTerm nextSolarTerm, SolarTerm recordSolarTerm, int recordPeriod) {
        boolean recordable = recordSolarTerm != null;
        SolarTermDto currentTerm = SolarTermDto.build(currentSolarTerm);
        SolarTermDto nextTerm = SolarTermDto.build(nextSolarTerm);

        SolarTermDto recordTerm = null;
        if(recordable) {
            recordTerm = new SolarTermDto(
                recordSolarTerm.getSequence(),
                recordSolarTerm.getDate().plusDays(recordPeriod));
        }

        return new FindSolarTermInfoResponse(recordable, currentTerm, nextTerm, recordTerm);
    }
}
