package today.seasoning.seasoning.friendship.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.common.enums.FriendshipStatus;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.friendship.domain.FriendRequestRepository;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;
import today.seasoning.seasoning.friendship.dto.SearchUserResult;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@Service
@RequiredArgsConstructor
public class SearchUserService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendRequestRepository friendRequestRepository;

    @Transactional(readOnly = true)
    public SearchUserResult doService(Long userId, String friendAccountId) {
        User friend = userRepository.findByAccountId(friendAccountId)
            .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "검색 결과 없음"));

        if (!friend.isSearchable()) {
            throw new CustomException(HttpStatus.NOT_FOUND, "검색 결과 없음");
        }

        FriendshipStatus friendshipStatus = resolveFriendshipStatus(userId, friend);

        return SearchUserResult.build(friend, friendshipStatus);
    }

    private FriendshipStatus resolveFriendshipStatus(Long userId, User friend) {
        // 나 자신
        if (userId.equals(friend.getId())) {
            return FriendshipStatus.SELF;
        }

        // 친구 상태
        if (friendshipRepository.existsByUserIdAndFriendId(userId, friend.getId())) {
            return FriendshipStatus.FRIEND;
        }

        // 친구 요청 받은 상태
        if (friendRequestRepository.existsByFromUserIdAndToUserId(friend.getId(), userId)) {
            return FriendshipStatus.RECEIVED;
        }

        // 친구 요청 상태
        if (friendRequestRepository.existsByFromUserIdAndToUserId(userId, friend.getId())) {
            return FriendshipStatus.SENT;
        }

        return FriendshipStatus.UNFRIEND;
    }
}
