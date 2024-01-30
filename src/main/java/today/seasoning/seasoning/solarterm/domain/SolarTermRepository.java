package today.seasoning.seasoning.solarterm.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolarTermRepository extends JpaRepository<SolarTerm, Long> {

    List<SolarTerm> findAllByOrderByDateAsc();
}
