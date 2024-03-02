package today.seasoning.seasoning.fortune.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import today.seasoning.seasoning.user.domain.User;

import java.util.Optional;

public interface FortuneUserRelationRepository extends JpaRepository<FortuneUserRelation, Long> {
    Optional<FortuneUserRelation> findByUser(User user);
}
