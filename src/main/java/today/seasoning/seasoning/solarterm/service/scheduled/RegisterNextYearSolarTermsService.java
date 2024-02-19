package today.seasoning.seasoning.solarterm.service.scheduled;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import today.seasoning.seasoning.common.aws.SnsService;
import today.seasoning.seasoning.solarterm.service.RegisterSolarTermsService;

@Service
@RequiredArgsConstructor
public class RegisterNextYearSolarTermsService {

    private final SnsService snsService;
    private final RegisterSolarTermsService registerSolarTermsService;

    @Scheduled(cron = "0 0 0 1 12 *")
    public void doService() {
        int nextYear = LocalDate.now().getYear() + 1;

        try {
            registerSolarTermsService.doService(nextYear);

            snsService.publish("[시즈닝] " + nextYear + "년 절기 등록 완료");
        } catch (Exception e) {
            snsService.publish("[시즈닝] ERROR - " + nextYear + "년 절기 등록 실패");
        }
    }

}
