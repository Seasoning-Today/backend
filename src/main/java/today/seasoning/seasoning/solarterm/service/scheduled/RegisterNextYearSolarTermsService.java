package today.seasoning.seasoning.solarterm.service.scheduled;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import today.seasoning.seasoning.common.aspect.NotifyResult;
import today.seasoning.seasoning.solarterm.service.RegisterSolarTermsService;

@Service
@RequiredArgsConstructor
public class RegisterNextYearSolarTermsService {

    private final RegisterSolarTermsService registerSolarTermsService;

    @Scheduled(cron = "0 0 0 1 12 *")
    @NotifyResult(name = "절기 등록 작업")
    public void doService() {
        int nextYear = LocalDate.now().getYear() + 1;
        registerSolarTermsService.doService(nextYear);
    }

}
