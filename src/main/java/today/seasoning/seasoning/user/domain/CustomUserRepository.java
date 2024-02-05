package today.seasoning.seasoning.user.domain;

import org.springframework.stereotype.Repository;

@Repository
public interface CustomUserRepository {

    User findByIdOrElseThrow(Long id);
}
