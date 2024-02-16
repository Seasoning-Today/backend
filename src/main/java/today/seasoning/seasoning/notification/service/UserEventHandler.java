package today.seasoning.seasoning.notification.service;

import static today.seasoning.seasoning.notification.domain.NotificationType.FRIENDSHIP_ACCEPTED;
import static today.seasoning.seasoning.notification.domain.NotificationType.FRIENDSHIP_REQUEST;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import today.seasoning.seasoning.article.event.ArticleLikedEvent;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.friendship.event.FriendRequestAcceptedEvent;
import today.seasoning.seasoning.friendship.event.FriendRequestCanceledEvent;
import today.seasoning.seasoning.friendship.event.FriendRequestDeclinedEvent;
import today.seasoning.seasoning.friendship.event.FriendRequestSentEvent;
import today.seasoning.seasoning.notification.domain.NotificationType;
import today.seasoning.seasoning.notification.domain.UserNotification;
import today.seasoning.seasoning.notification.domain.UserNotificationRepository;

@Component
@RequiredArgsConstructor
public class UserEventHandler {

    private final UserNotificationRepository userNotificationRepository;

    @EventListener
    public void handleFriendRequestSentEvent(FriendRequestSentEvent event) {
        UserNotification notification = UserNotification.builder()
            .senderId(event.getFromUserId())
            .receiverId(event.getToUserId())
            .type(FRIENDSHIP_REQUEST)
            .build();

        userNotificationRepository.save(notification);
    }

    @EventListener
    public void handleFriendRequestCanceledEvent(FriendRequestCanceledEvent event) {
        deleteFriendRequestNotification(event.getFromUserId(), event.getToUserId());
    }

    @EventListener
    public void handleFriendRequestAcceptedEvent(FriendRequestAcceptedEvent event) {
        UserNotification notification = UserNotification.builder()
            .senderId(event.getToUserId())
            .receiverId(event.getFromUserId())
            .type(FRIENDSHIP_ACCEPTED)
            .build();

        userNotificationRepository.save(notification);
        deleteFriendRequestNotification(event.getFromUserId(), event.getToUserId());
    }

    @EventListener
    public void handleFriendRequestDeclinedEvent(FriendRequestDeclinedEvent event) {
        deleteFriendRequestNotification(event.getFromUserId(), event.getToUserId());
    }

    private void deleteFriendRequestNotification(Long fromUserId, Long toUserId) {
        userNotificationRepository.delete(fromUserId, toUserId, FRIENDSHIP_REQUEST);
    }

    @EventListener
    public void handleArticleLikedEvent(ArticleLikedEvent event) {
        UserNotification notification = UserNotification.builder()
            .senderId(event.getUserId())
            .receiverId(event.getAuthorId())
            .type(NotificationType.ARTICLE_FEEDBACK)
            .message(TsidUtil.toString(event.getArticleId()))
            .build();

        userNotificationRepository.save(notification);
    }

}
