package today.seasoning.seasoning.friendship.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.common.util.EntitySerializationUtil;
import today.seasoning.seasoning.friendship.domain.FriendRequest;
import today.seasoning.seasoning.friendship.domain.FriendRequestRepository;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;
import today.seasoning.seasoning.notification.domain.NotificationType;
import today.seasoning.seasoning.notification.service.NotificationService;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;
import today.seasoning.seasoning.user.dto.UserProfileDto;

@Service
@Transactional
@RequiredArgsConstructor
public class SendFriendRequestService {

	private final UserRepository userRepository;
	private final NotificationService notificationService;
	private final FriendshipRepository friendshipRepository;
	private final FriendRequestRepository friendRequestRepository;

	public void doService(Long fromUserId, Long toUserId) {
		User fromUser = userRepository.findByIdOrElseThrow(fromUserId);
		User toUser = userRepository.findByIdOrElseThrow(toUserId);

		checkException(fromUser, toUser);

		friendRequestRepository.save(new FriendRequest(fromUser, toUser));

		registerNotification(fromUser, toUser);
	}

	private void checkException(User fromUser, User toUser) {
		// 자신에게 친구 요청한 경우
		if (fromUser == toUser) {
			throw new CustomException(HttpStatus.BAD_REQUEST, "Invalid Request");
		}

		// 이미 친구 신청을 한 경우
		if (friendRequestRepository.existsByFromUserIdAndToUserId(fromUser.getId(), toUser.getId())) {
			throw new CustomException(HttpStatus.CONFLICT, "Friend request already sent");
		}

		// 이미 친구인 경우
		if (friendshipRepository.existsByUserIdAndFriendId(fromUser.getId(), toUser.getId())) {
			throw new CustomException(HttpStatus.CONFLICT, "Already friends with this user");
		}
	}

	private void registerNotification(User fromUser, User toUser) {
		String message = EntitySerializationUtil.serialize(UserProfileDto.build(fromUser));
		notificationService.registerNotification(toUser.getId(), NotificationType.FRIENDSHIP_REQUEST, message);
	}
}
