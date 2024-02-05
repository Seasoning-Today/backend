package today.seasoning.seasoning.friendship.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.friendship.domain.Friendship;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class UnfriendService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    public void doService(Long userId, Long friendUserId) {
        User friend = userRepository.findById(friendUserId)
            .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "회원 조회 실패"));

        deleteFriendship(userId, friend.getId());
        deleteFriendship(friend.getId(), userId);
    }

    private void deleteFriendship(Long userId, Long friendId) {
        Friendship friendship = friendshipRepository.findByUserIdAndFriendId(userId, friendId)
            .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid Friendship"));

        friendshipRepository.delete(friendship);
    }
}
