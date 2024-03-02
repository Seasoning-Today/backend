package today.seasoning.seasoning.fortune.service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.fortune.domain.Fortune;
import today.seasoning.seasoning.fortune.domain.FortuneRepository;
import today.seasoning.seasoning.fortune.domain.FortuneUserRelation;
import today.seasoning.seasoning.fortune.domain.FortuneUserRelationRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class FindFortuneService {

    private static final SecureRandom secureRandom = new SecureRandom();

    private final FortuneRepository fortuneRepository;
    private final FortuneUserRelationRepository fortuneUserRelationRepository;

    public String doService(Long userId) {
        List<Fortune> fortunes = fortuneRepository.findAll();

        FortuneUserRelation fortuneUserRelation = fortuneUserRelationRepository.findByUserId(userId)
            .orElseGet(() -> fortuneUserRelationRepository.save(new FortuneUserRelation(userId, getRandomFortune(fortunes))));

        if (fortuneUserRelation.getLastModifiedDate().isBefore(LocalDate.now())) {
            fortunes.remove(fortuneUserRelation.getFortune());
            fortuneUserRelation.changeFortune(getRandomFortune(fortunes));
        }

        return fortuneUserRelation.getFortune().getContent();
    }

    private Fortune getRandomFortune(List<Fortune> fortunes) {
        return fortunes.get(secureRandom.nextInt(fortunes.size()));
    }
}
