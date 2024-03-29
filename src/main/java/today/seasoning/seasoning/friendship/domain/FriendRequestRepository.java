package today.seasoning.seasoning.friendship.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    boolean existsByFromUserIdAndToUserId(Long fromUserId, Long toUserId);

    void deleteByFromUserIdAndToUserId(Long fromUserId, Long toUserId);
}
