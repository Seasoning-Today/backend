package today.seasoning.seasoning.notification.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import today.seasoning.seasoning.notification.dto.UserNotificationProjectionInterface;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    @Modifying
    @Query("DELETE FROM UserNotification n WHERE n.senderId = :senderId AND n.receiverId = :receiverId AND n.type = :type")
    void delete(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId, @Param("type") NotificationType type);

    @Query(value = "SELECT n.id as id, n.type as type, n.created_date as created, u.id as userId, u.nickname as userNickname, u.account_id as userAccountId, u.profile_image_url as userImageUrl, n.message as message, n.is_read as isRead " +
        "FROM user_notification n " +
        "INNER JOIN user u ON n.sender_id = u.id " +
        "WHERE n.receiver_id = :receiverId AND n.id < :lastId " +
        "ORDER BY n.id DESC LIMIT :size", nativeQuery = true)
    List<UserNotificationProjectionInterface> find(@Param("receiverId") Long receiverId, @Param("lastId") long lastId, @Param("size") int size);

    @Modifying
    @Query("UPDATE UserNotification n SET n.read = true WHERE n.id = :id")
    void markAsRead(@Param("id") Long id);

    List<UserNotification> findByReceiverId(Long receiverId);

    @Query("SELECT COUNT(n) > 0 FROM UserNotification n WHERE n.receiverId = :receiverId AND n.read = false")
    boolean checkUnreadNotificationsExist(@Param("receiverId") Long receiverId);
}
