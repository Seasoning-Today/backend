package today.seasoning.seasoning.friendship.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import today.seasoning.seasoning.user.domain.User;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

	@Query("SELECT f FROM Friendship f WHERE f.user.id = :userId AND f.friend.id = :friendId")
	Optional<Friendship> findByUserIdAndFriendId(@Param("userId") Long userId, @Param("friendId") Long friendId);

	@Query("SELECT f.friend FROM Friendship f WHERE f.user.id = :userId")
	List<User> findFriendsByUserId(@Param("userId") Long userId);

	boolean existsByUserIdAndFriendId(Long userId, Long friendId);

}
