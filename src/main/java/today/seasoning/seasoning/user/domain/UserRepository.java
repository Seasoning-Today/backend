package today.seasoning.seasoning.user.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import today.seasoning.seasoning.common.enums.LoginType;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {

	@Query("SELECT u FROM User u WHERE u.email = :email AND u.loginType = :loginType")
	Optional<User> find(@Param("email") String email, @Param("loginType") LoginType loginType);

	@Query("SELECT u FROM User u WHERE u.accountId = :accountId")
	Optional<User> findByAccountId(@Param("accountId") String accountId);

	boolean existsByAccountId(String accountId);
}
