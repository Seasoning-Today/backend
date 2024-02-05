package today.seasoning.seasoning.solarterm.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.notification.service.NotificationService;
import today.seasoning.seasoning.solarterm.domain.SolarTerm;
import today.seasoning.seasoning.solarterm.domain.SolarTermRepository;
import today.seasoning.seasoning.solarterm.dto.FindSolarTermInfoResponse;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SolarTermService {

    // 날짜상 현재 절기와 다음 절기
    private SolarTerm currentSolarTerm;
    private SolarTerm nextSolarTerm;
    // 현재 기록장이 열린 절기
    private Optional<SolarTerm> recordSolarTerm;

    @Value("${ARTICLE-REGISTRATION-PERIOD}")
    private int ARTICLE_REGISTRATION_PERIOD;

    private final SolarTermRepository solarTermRepository;
    private final NotificationService notificationService;

    @PostConstruct
    protected void init() {
        updateSolarTerms();
    }

    @Scheduled(cron = "5 0 0 * * *")
    protected void dailyUpdate() {
        Optional<SolarTerm> pastRecordSolarTerm = recordSolarTerm;

        updateSolarTerms();

        if (pastRecordSolarTerm.isEmpty() && recordSolarTerm.isPresent()) {
            notificationService.registerArticleOpenNotification(recordSolarTerm.get().getSequence());
        }
    }

    protected void updateSolarTerms() {
        List<SolarTerm> solarTerms = solarTermRepository.findAllByOrderByDateAsc();

        LocalDate today = LocalDate.now();

        for (int i = 0; i + 1 < solarTerms.size(); i++) {
            if (!today.isBefore(solarTerms.get(i).getDate()) && today.isBefore(solarTerms.get(i + 1).getDate())) {
                currentSolarTerm = solarTerms.get(i);
                nextSolarTerm = solarTerms.get(i + 1);

                // 현재 기록장이 열린 절기를 계산
                if (currentSolarTerm.getDaysDiff(today) <= ARTICLE_REGISTRATION_PERIOD) {
                    recordSolarTerm = Optional.of(currentSolarTerm);
                } else if (nextSolarTerm.getDaysDiff(today) <= ARTICLE_REGISTRATION_PERIOD) {
                    recordSolarTerm = Optional.of(nextSolarTerm);
                } else {
                    recordSolarTerm = Optional.empty();
                }

                break;
            }
        }

        log.info("현재 절기 : {} / 다음 절기 : {} / 열린 절기 : {}",
            currentSolarTerm.getSequence(),
            nextSolarTerm.getSequence(),
            recordSolarTerm.map(SolarTerm::getSequence).orElse(-1));
    }

    public Optional<SolarTerm> findRecordSolarTerm() {
        return recordSolarTerm;
    }

    public FindSolarTermInfoResponse findSolarTermInfo() {
        return FindSolarTermInfoResponse.build(currentSolarTerm, nextSolarTerm, recordSolarTerm.orElse(null),
            ARTICLE_REGISTRATION_PERIOD);
    }
}
