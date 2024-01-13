package today.seasoning.seasoning.solarterm.service;

import java.time.LocalDate;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.notification.service.NotificationService;
import today.seasoning.seasoning.solarterm.domain.SolarTerm;
import today.seasoning.seasoning.solarterm.domain.SolarTermRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class SolarTermService {

    // 현재 절기 순번 : 1 ~ 24 (-1 : 절기가 아님)
    private static int currentOpenTerm;

    @Value("${ARTICLE-REGISTRATION-PERIOD}")
    private int ARTICLE_REGISTRATION_PERIOD;

    private final SolarTermRepository solarTermRepository;
    private final NotificationService notificationService;

    @PostConstruct
    @Scheduled(cron = "5 0 0 * * *")
    public void updateTerm() {
        int openTerm = calculateOpenTerm(LocalDate.now());

        if (currentOpenTerm < openTerm) {
            notificationService.registerArticleOpenNotification(openTerm);
        }
        currentOpenTerm = openTerm;
    }

    private int calculateOpenTerm(LocalDate date) {
        List<SolarTerm> solarTerms = solarTermRepository.findByYearAndMonth(date.getYear(), date.getMonthValue());

        for (SolarTerm solarTerm : solarTerms) {
            int daysDifferenceFromTerm = Math.abs(solarTerm.getDay() - date.getDayOfMonth());
            if (daysDifferenceFromTerm < ARTICLE_REGISTRATION_PERIOD) {
                return solarTerm.getSequence();
            }
        }
        return -1;
    }

    public int findCurrentTerm() {
        return currentOpenTerm;
    }

    public boolean checkRecordOpen() {
        return currentOpenTerm > 0;
    }

}
