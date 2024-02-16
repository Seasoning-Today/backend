package today.seasoning.seasoning.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.notification.domain.UserNotificationRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CheckUnreadNotificationsExistService {

    private final UserNotificationRepository userNotificationRepository;

    public boolean doService(Long userId) {
        return userNotificationRepository.checkUnreadNotificationsExist(userId);
    }
}
