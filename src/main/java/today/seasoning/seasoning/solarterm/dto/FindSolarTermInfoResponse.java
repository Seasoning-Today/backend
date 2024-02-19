package today.seasoning.seasoning.solarterm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import today.seasoning.seasoning.solarterm.domain.SolarTerm;

@Getter
@AllArgsConstructor
public class FindSolarTermInfoResponse {

    private final boolean recordable;
    private final SolarTermDto currentTerm;
    private final SolarTermDto nextTerm;
    private final SolarTermDto recordTerm;

    public static FindSolarTermInfoResponse build(
        SolarTerm currentSolarTerm,
        SolarTerm nextSolarTerm,
        SolarTerm recordSolarTerm,
        int recordPeriod
    ) {
        boolean recordable = recordSolarTerm != null;
        SolarTermDto currentTerm = currentSolarTerm != null ? SolarTermDto.build(currentSolarTerm) : null;
        SolarTermDto nextTerm = nextSolarTerm != null ? SolarTermDto.build(nextSolarTerm) : null;

        SolarTermDto recordTerm = null;
        if (recordable) {
            recordTerm = new SolarTermDto(recordSolarTerm.getSequence(),
                recordSolarTerm.getDate().plusDays(recordPeriod));
        }

        return new FindSolarTermInfoResponse(recordable, currentTerm, nextTerm, recordTerm);
    }
}
