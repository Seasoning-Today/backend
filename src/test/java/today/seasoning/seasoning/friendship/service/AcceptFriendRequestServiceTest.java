package today.seasoning.seasoning.friendship.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.friendship.domain.FriendRequestRepository;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;
import today.seasoning.seasoning.notification.service.NotificationService;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@DisplayName("친구 수락 서비스")
@ExtendWith(MockitoExtension.class)
class AcceptFriendRequestServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    NotificationService notificationService;
    @Mock
    FriendshipRepository friendshipRepository;
    @Mock
    FriendRequestRepository friendRequestRepository;
    @InjectMocks
    AcceptFriendRequestService acceptFriendRequestService;

    User user = new User("user", "https://test.com/user.jpg", "user@email.com", LoginType.KAKAO);
    User requester = new User("requester", "https://test.com/requester.jpg", "requester@email.com", LoginType.KAKAO);

    @BeforeEach
    void init() {
        given(userRepository.findByIdOrElseThrow(user.getId()))
            .willReturn(user);
    }

    @Test
    @DisplayName("성공")
    void success() {
        //given : 아이디로 상대방이 조회되고, 상대방이 나에게 친구 요청한 경우
        given(userRepository.findByIdOrElseThrow(requester.getId()))
            .willReturn(requester);

        given(friendRequestRepository.existsByFromUserIdAndToUserId(requester.getId(), user.getId()))
            .willReturn(true);

        //when & then : 친구 수락 시, 예외가 발생하지 않으며
        assertDoesNotThrow(() -> acceptFriendRequestService.doService(user.getId(), requester.getId()));
        // 서로의 친구 요청 내역이 삭제되어야 한다
        verify(friendRequestRepository).deleteByFromUserIdAndToUserId(requester.getId(), user.getId());
        verify(friendRequestRepository).deleteByFromUserIdAndToUserId(user.getId(), requester.getId());
    }

    @Test
    @DisplayName("실패 - 상대방 조회 불가")
    void failedByRequesterNotFound() {
        //given : 아이디에 해당하는 상대방이 존재하지 않으면
        given(userRepository.findByIdOrElseThrow(requester.getId()))
            .willThrow(new CustomException(HttpStatus.BAD_REQUEST, "User Not Found"));

        //when & then : Bad Request 예외가 발생한다
        assertFailedValidation(user.getId(), requester.getId(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("실패 - 신청 내역 조회 불가")
    void failedByFriendRequestNotFound() {
        //given : 상대방으로부터 나에게 온 친구 신청 내역이 없으면
        given(friendRequestRepository.existsByFromUserIdAndToUserId(requester.getId(), user.getId()))
            .willReturn(false);

        given(userRepository.findByIdOrElseThrow(requester.getId()))
            .willReturn(requester);

        //when & then : Bad Request 예외가 발생한다
        assertFailedValidation(user.getId(), requester.getId(), HttpStatus.BAD_REQUEST);
    }

    private void assertFailedValidation(Long userId, Long requesterUserId, HttpStatus httpStatus) {
        assertThatThrownBy(() -> acceptFriendRequestService.doService(userId, requesterUserId))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("httpStatus", httpStatus);
    }
}
