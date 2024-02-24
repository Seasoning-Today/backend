package today.seasoning.seasoning.solarterm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import today.seasoning.seasoning.solarterm.domain.SolarTerm;

@Getter
@AllArgsConstructor
@Schema(title = "절기 조회 응답")
public class FindSolarTermInfoResponse {

    @Schema(description = "기록장 열림 여부")
    private final boolean recordable;
    @Schema(description = "시간상 현재 절기")
    private final SolarTermDto currentTerm;
    @Schema(description = "시간상 다음 절기")
    private final SolarTermDto nextTerm;
    @Schema(description = "기록장 등록 가능한 절기")
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
