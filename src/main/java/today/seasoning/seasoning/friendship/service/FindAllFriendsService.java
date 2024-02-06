package today.seasoning.seasoning.friendship.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;
import today.seasoning.seasoning.friendship.dto.FindUserFriendsResponse;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindAllFriendsService {

    private final FriendshipRepository friendshipRepository;

    public List<FindUserFriendsResponse> doService(Long userId) {
        return friendshipRepository.findFriendsByUserId(userId)
            .stream()
            .map(FindUserFriendsResponse::build)
            .collect(Collectors.toList());
    }
}
