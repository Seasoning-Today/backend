package today.seasoning.seasoning.friendship.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;
import today.seasoning.seasoning.friendship.dto.FindUserFriendsResult;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindUserFriendsService {

    private final FriendshipRepository friendshipRepository;

    public List<FindUserFriendsResult> doFind(Long userId) {
        return friendshipRepository.findFriendsByUserId(userId)
            .stream()
            .map(FindUserFriendsResult::build)
            .collect(Collectors.toList());
    }
}
