package today.seasoning.seasoning.fortune.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FortuneUserRelationRepository extends JpaRepository<FortuneUserRelation, Long> {

    Optional<FortuneUserRelation> findByUserId(Long userId);
}
