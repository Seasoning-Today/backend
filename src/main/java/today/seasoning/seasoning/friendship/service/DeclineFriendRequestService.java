package today.seasoning.seasoning.friendship.service;


import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.friendship.domain.FriendRequestRepository;
import today.seasoning.seasoning.friendship.event.FriendRequestDeclinedEvent;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class DeclineFriendRequestService {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void doService(Long userId, Long requesterUserId) {
        User requester = userRepository.findByIdOrElseThrow(requesterUserId);

        if (!friendRequestRepository.existsByFromUserIdAndToUserId(requester.getId(), userId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "신청 내역 없음");
        }

        friendRequestRepository.deleteByFromUserIdAndToUserId(requester.getId(), userId);

        applicationEventPublisher.publishEvent(new FriendRequestDeclinedEvent(requester.getId(), userId));
    }
}
