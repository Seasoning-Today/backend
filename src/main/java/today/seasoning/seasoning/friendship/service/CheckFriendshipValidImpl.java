package today.seasoning.seasoning.friendship.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;

@Service
@RequiredArgsConstructor
public class CheckFriendshipValidImpl implements CheckFriendshipValid {

    private final FriendshipRepository friendshipRepository;

    @Override
    public boolean doCheck(Long userId, Long friendId) {
        return friendshipRepository.existsByUserIdAndFriendId(userId, friendId);
    }
}
