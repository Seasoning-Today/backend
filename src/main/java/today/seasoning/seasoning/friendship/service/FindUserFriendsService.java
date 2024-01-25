package today.seasoning.seasoning.friendship.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.friendship.domain.Friendship;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;
import today.seasoning.seasoning.friendship.dto.FindUserFriendsResult;
import today.seasoning.seasoning.user.domain.User;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindUserFriendsService {

	private final FriendshipRepository friendshipRepository;
	private final CheckFriendshipValid checkFriendshipValid;

	public List<FindUserFriendsResult> doFind(Long userId) {
		return friendshipRepository.findByFriendId(userId)
			.stream()
			.map(Friendship::getUser)
			.filter(friend -> checkFriendshipValid.doCheck(userId, friend.getId()))
			.map(this::createFindUserFriendsResult)
			.collect(Collectors.toList());
	}

	private FindUserFriendsResult createFindUserFriendsResult(User friend) {
		return new FindUserFriendsResult(
			friend.getNickname(),
			friend.getAccountId(),
			friend.getProfileImageUrl());
	}
}
