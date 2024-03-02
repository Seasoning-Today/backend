package today.seasoning.seasoning.fortune.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.fortune.domain.Fortune;
import today.seasoning.seasoning.fortune.domain.FortuneRepository;
import today.seasoning.seasoning.fortune.domain.FortuneUserRelation;
import today.seasoning.seasoning.fortune.domain.FortuneUserRelationRepository;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class FindFortuneService {
    private final FortuneRepository fortuneRepository;
    private final FortuneUserRelationRepository fortuneUserRelationRepository;
    private final UserRepository userRepository;


    public String doService(Long userId) {
        User user = userRepository.findById(userId).get();
        FortuneUserRelation fortuneUserRelation = fortuneUserRelationRepository.findByUser(user).orElse(null);

        LocalDate today = LocalDate.now();
        LocalDate getFortuneDate;
        Fortune fortune;

        // 운세가 있고 오늘 조회한것이라면 기존 운세 반환
        if (fortuneUserRelation != null) {
            getFortuneDate = fortuneUserRelation.getDate();
            if (getFortuneDate.isBefore(today)) {
                fortune = findRandomFortune();
                fortuneUserRelation.updateFortune(fortune);
            } else
                fortune = fortuneUserRelation.getFortune();
        } else { // 운세가 아예 없다면 새로 저장
            fortune = findRandomFortune();
            fortuneUserRelationRepository.save(new FortuneUserRelation(user, fortune));
        }
        return fortune.getContent();
    }

    private Fortune findRandomFortune() {
        List<Fortune> all = fortuneRepository.findAll();
        Random random = new Random();
        Fortune fortune = all.get(random.nextInt(all.size()));
        return fortune;
    }
}
