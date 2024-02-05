package today.seasoning.seasoning.notification.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.notification.domain.Notification;
import today.seasoning.seasoning.notification.domain.NotificationRepository;
import today.seasoning.seasoning.notification.domain.NotificationType;
import today.seasoning.seasoning.notification.dto.FindNotificationCommand;
import today.seasoning.seasoning.notification.dto.NotificationDto;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

	private final UserRepository userRepository;
	private final EntityManager entityManager;
	private final NotificationRepository notificationRepository;

	public void registerNotification(Long receiveUserId, NotificationType type, String message) {
		User user = userRepository.findByIdOrElseThrow(receiveUserId);

		Notification notification = Notification.create(type, user, message);
		notificationRepository.save(notification);
	}

	public List<NotificationDto> findNotifications(FindNotificationCommand command) {
		String sql = "SELECT n FROM Notification n WHERE n.user.id = :userId "
			+ "AND n.id < :notificationId "
			+ "ORDER BY n.id DESC";

		List<Notification> notificationList = entityManager.createQuery(sql, Notification.class)
			.setParameter("userId", command.getUserId())
			.setParameter("notificationId", command.getLastReadNotificationId())
			.setMaxResults(command.getPageSize())
			.getResultList();

		List<NotificationDto> notificationDtos = notificationList.stream()
			.map(NotificationDto::build)
			.collect(Collectors.toList());

		markNotificationsAsRead(notificationList);

		return notificationDtos;
	}

	public void registerArticleOpenNotification(int term) {
		userRepository.findAll()
			.forEach(user -> registerNotification(user.getId(),
				NotificationType.ARTICLE_OPEN,
				String.valueOf(term)));
	}

	private void markNotificationsAsRead(List<Notification> sentNotificationList) {
		sentNotificationList.forEach(Notification::markAsRead);
		notificationRepository.saveAll(sentNotificationList);
	}
}
