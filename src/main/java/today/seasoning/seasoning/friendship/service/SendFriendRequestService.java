package today.seasoning.seasoning.friendship.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.common.util.EntitySerializationUtil;
import today.seasoning.seasoning.friendship.domain.FriendRequest;
import today.seasoning.seasoning.friendship.domain.FriendRequestRepository;
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
	private final FriendRequestRepository friendRequestRepository;

	public void doService(Long fromUserId, String toUserAccountId) {
		User fromUser = userRepository.findById(fromUserId).get();

		User toUser = userRepository.findByAccountId(toUserAccountId)
			.orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "상대방 조회 실패"));

		checkException(fromUser, toUser);

		friendRequestRepository.save(new FriendRequest(fromUser, toUser));

		registerNotification(toUser, fromUser);
	}

	private void checkException(User fromUser, User toUser) {
		if (fromUser == toUser) {
			throw new CustomException(HttpStatus.BAD_REQUEST, "Invalid Request");
		}

		if (friendRequestRepository.existsByFromUserIdAndToUserId(fromUser.getId(), toUser.getId())) {
			throw new CustomException(HttpStatus.CONFLICT, "Already Requested");
		}
	}

	private void registerNotification(User fromUser, User toUser) {
		String message = EntitySerializationUtil.serialize(UserProfileDto.build(fromUser));
		notificationService.registerNotification(toUser.getId(), NotificationType.FRIENDSHIP_REQUEST, message);
	}
}
