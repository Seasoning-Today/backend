package today.seasoning.seasoning.friendship.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.friendship.domain.FriendRequestRepository;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CancelFriendRequestService {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    public void doService(Long userId, Long requesteeUserId) {
        User requestee = userRepository.findById(requesteeUserId)
            .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "상대방 조회 실패"));

        if (!friendRequestRepository.existsByFromUserIdAndToUserId(userId, requestee.getId())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "신청 내역 없음");
        }

        friendRequestRepository.deleteByFromUserIdAndToUserId(userId, requestee.getId());
    }
}
