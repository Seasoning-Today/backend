package today.seasoning.seasoning.friendship.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.friendship.domain.FriendRequestRepository;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@DisplayName("친구 신청 거부 서비스")
@ExtendWith(MockitoExtension.class)
class DeclineFriendRequestServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    FriendRequestRepository friendRequestRepository;
    @Mock
    ApplicationEventPublisher applicationEventPublisher;
    @InjectMocks
    DeclineFriendRequestService declineFriendRequestService;

    User user = new User("user", "https://test.com/user.jpg", "user@email.com", LoginType.KAKAO);
    User requester = new User("requester", "https://test.com/requester.jpg", "requester@email.com", LoginType.KAKAO);

    @Test
    @DisplayName("성공")
    void success() {
        //given : 상대방이 조회되고, 상대로부터 나에게 온 친구 신청 내역이 존재하면
        given(userRepository.findByIdOrElseThrow(requester.getId()))
            .willReturn(requester);

        given(friendRequestRepository.existsByFromUserIdAndToUserId(requester.getId(), user.getId()))
            .willReturn(true);

        //when & then : 예외가 발생하지 않는다
        assertDoesNotThrow(() -> declineFriendRequestService.doService(user.getId(), requester.getId()));
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
        //given : 상대방이 나에게 친구 신청한 내역이 없으면
        given(friendRequestRepository.existsByFromUserIdAndToUserId(requester.getId(), user.getId()))
            .willReturn(false);

        given(userRepository.findByIdOrElseThrow(requester.getId()))
            .willReturn(requester);

        //when & then : Bad Request 예외가 발생한다
        assertFailedValidation(user.getId(), requester.getId(), HttpStatus.BAD_REQUEST);
    }

    private void assertFailedValidation(Long userId, Long requesterUserId, HttpStatus httpStatus) {
        assertThatThrownBy(() -> declineFriendRequestService.doService(userId, requesterUserId))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("httpStatus", httpStatus);
    }
}
