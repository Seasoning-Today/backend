package today.seasoning.seasoning.notification.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.notification.domain.UserNotificationRepository;
import today.seasoning.seasoning.notification.dto.FindNotificationCommand;
import today.seasoning.seasoning.notification.dto.UserNotificationProjectionInterface;
import today.seasoning.seasoning.notification.dto.UserNotificationResponse;

@Service
@Transactional
@RequiredArgsConstructor
public class FindNotificationsService {

    private final UserNotificationRepository userNotificationRepository;

    public List<UserNotificationResponse> doService(FindNotificationCommand command) {
        List<UserNotificationProjectionInterface> projectionInterfaces = userNotificationRepository.find(
            command.getUserId(), command.getLastId(), command.getPageSize());

        List<UserNotificationResponse> notificationResponses = projectionInterfaces.stream()
            .map(UserNotificationResponse::build)
            .collect(Collectors.toList());

        setNotificationsAsRead(projectionInterfaces);

        return notificationResponses;
    }

    private void setNotificationsAsRead(List<UserNotificationProjectionInterface> projectionInterfaces) {
        projectionInterfaces.stream()
            .map(UserNotificationProjectionInterface::getId)
            .forEach(userNotificationRepository::markAsRead);
    }
}
