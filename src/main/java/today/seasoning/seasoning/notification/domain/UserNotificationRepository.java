package today.seasoning.seasoning.notification.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    @Modifying
    @Query("DELETE FROM UserNotification n WHERE n.senderId = :senderId AND n.receiverId = :receiverId AND n.type = :type")
    void delete(@Param("senderId") Long sender, @Param("receiverId") Long receiverId, @Param("type") NotificationType type);

}
