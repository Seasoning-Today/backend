package today.seasoning.seasoning.notification.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerNotificationRepository extends JpaRepository<ServerNotification, Long> {

}
