package today.seasoning.seasoning.friendship.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

	@Query("SELECT f FROM Friendship f WHERE f.user.id = :userId AND f.friend.id = :friendId")
	Optional<Friendship> findByUserIds(
		@Param("userId") Long userId,
		@Param("friendId") Long friendId
	);

	@Query("SELECT f FROM Friendship f WHERE f.user.id = :friendId")
	List<Friendship> findByFriendId(@Param("friendId") Long friendId);

}
