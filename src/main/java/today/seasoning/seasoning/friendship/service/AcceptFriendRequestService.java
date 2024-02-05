package today.seasoning.seasoning.friendship.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.common.util.EntitySerializationUtil;
import today.seasoning.seasoning.friendship.domain.FriendRequestRepository;
import today.seasoning.seasoning.friendship.domain.Friendship;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;
import today.seasoning.seasoning.notification.domain.NotificationType;
import today.seasoning.seasoning.notification.service.NotificationService;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;
import today.seasoning.seasoning.user.dto.UserProfileDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class AcceptFriendRequestService {

    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final FriendshipRepository friendshipRepository;
    private final FriendRequestRepository friendRequestRepository;

    @Transactional
    public void doService(Long userId, Long requestUserId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "회원 조회 실패"));

        User requester = userRepository.findById(requestUserId)
            .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "상대방 조회 실패"));

        if (!friendRequestRepository.existsByFromUserIdAndToUserId(requester.getId(), user.getId())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "신청 내역 없음");
        }

        // 친구 관계 설정
        friendshipRepository.save(new Friendship(user, requester));
        friendshipRepository.save(new Friendship(requester, user));

        // 친구 요청 내역 삭제
        friendRequestRepository.deleteByFromUserIdAndToUserId(requester.getId(), user.getId());
        friendRequestRepository.deleteByFromUserIdAndToUserId(user.getId(), requester.getId());

        // 알림 등록
        registerNotifications(user, requester);
    }

    private void registerNotifications(User user, User requester) {
        String message = EntitySerializationUtil.serialize(UserProfileDto.build(user));
        notificationService.registerNotification(requester.getId(), NotificationType.FRIENDSHIP_ACCEPTED, message);
    }
}
