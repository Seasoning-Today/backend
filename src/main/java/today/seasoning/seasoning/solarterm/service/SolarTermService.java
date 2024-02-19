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
import today.seasoning.seasoning.solarterm.domain.SolarTerm;
import today.seasoning.seasoning.solarterm.domain.SolarTermRepository;
import today.seasoning.seasoning.solarterm.dto.FindSolarTermInfoResponse;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SolarTermService {


    @Value("${ARTICLE-REGISTRATION-PERIOD}")
    private int ARTICLE_REGISTRATION_PERIOD;

    private final SolarTermRepository solarTermRepository;

    // 날짜상 현재 절기와 다음 절기
    private Optional<SolarTerm> currentSolarTerm = Optional.empty();
    private Optional<SolarTerm> nextSolarTerm = Optional.empty();
    // 현재 기록장이 열린 절기
    private Optional<SolarTerm> recordSolarTerm = Optional.empty();

    @PostConstruct
    protected void init() {
        updateSolarTerms();
    }

    @Scheduled(cron = "5 0 0 * * *")
    protected void dailyUpdate() {
        updateSolarTerms();
    }

    protected void updateSolarTerms() {
        List<SolarTerm> solarTerms = solarTermRepository.findAllByOrderByDateAsc();

        LocalDate today = LocalDate.now();

        for (int i = 0; i + 1 < solarTerms.size(); i++) {
            if (!today.isBefore(solarTerms.get(i).getDate()) && today.isBefore(solarTerms.get(i + 1).getDate())) {
                currentSolarTerm = Optional.of(solarTerms.get(i));
                nextSolarTerm = Optional.of(solarTerms.get(i + 1));

                // 현재 기록장이 열린 절기를 계산
                if (currentSolarTerm.get().getDaysDiff(today) <= ARTICLE_REGISTRATION_PERIOD) {
                    recordSolarTerm = currentSolarTerm;
                } else if (nextSolarTerm.get().getDaysDiff(today) <= ARTICLE_REGISTRATION_PERIOD) {
                    recordSolarTerm = nextSolarTerm;
                } else {
                    recordSolarTerm = Optional.empty();
                }

                break;
            }
        }

        log.info("현재 절기 : {} / 다음 절기 : {} / 열린 절기 : {}",
            currentSolarTerm.map(SolarTerm::getSequence).orElse(-1),
            nextSolarTerm.map(SolarTerm::getSequence).orElse(-1),
            recordSolarTerm.map(SolarTerm::getSequence).orElse(-1));
    }

    public Optional<SolarTerm> findRecordSolarTerm() {
        return recordSolarTerm;
    }

    public FindSolarTermInfoResponse findSolarTermInfo() {
        return FindSolarTermInfoResponse.build(
            currentSolarTerm.orElse(null),
            nextSolarTerm.orElse(null),
            recordSolarTerm.orElse(null),
            ARTICLE_REGISTRATION_PERIOD);
    }
}
