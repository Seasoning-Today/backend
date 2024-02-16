package today.seasoning.seasoning.friendship.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.friendship.domain.FriendRequestRepository;
import today.seasoning.seasoning.friendship.domain.Friendship;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;
import today.seasoning.seasoning.friendship.event.FriendRequestAcceptedEvent;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AcceptFriendRequestService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void doService(Long userId, Long requestUserId) {
        User user = userRepository.findByIdOrElseThrow(userId);
        User requester = userRepository.findByIdOrElseThrow(requestUserId);

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
        applicationEventPublisher.publishEvent(new FriendRequestAcceptedEvent(requester.getId(), user.getId()));
    }
}
