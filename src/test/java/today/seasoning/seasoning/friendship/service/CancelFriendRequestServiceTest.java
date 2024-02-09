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

@DisplayName("친구 신청 취소 서비스")
@ExtendWith(MockitoExtension.class)
class CancelFriendRequestServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    FriendRequestRepository friendRequestRepository;
    @Mock
    ApplicationEventPublisher applicationEventPublisher;
    @InjectMocks
    CancelFriendRequestService cancelFriendRequestService;

    User user = new User("user", "https://test.com/requester.jpg", "requester@email.com", LoginType.KAKAO);
    User requestee = new User("requestee", "https://test.com/requestee.jpg", "requestee@email.com", LoginType.KAKAO);

    @Test
    @DisplayName("성공")
    void success() {
        //given : 상대방이 존재하고, 내가 상대방에게 친구 신청한 내역이 있으면
        given(userRepository.findByIdOrElseThrow(requestee.getId()))
            .willReturn(requestee);

        given(friendRequestRepository.existsByFromUserIdAndToUserId(user.getId(), requestee.getId()))
            .willReturn(true);

        //when & then : 예외가 발생하지 않는다
        assertDoesNotThrow(() -> cancelFriendRequestService.doService(user.getId(), requestee.getId()));
    }

    @Test
    @DisplayName("실패 - 상대방 조회 불가")
    void failedByRequesterNotFound() {
        //given : 아이디에 해당하는 상대방이 존재하지 않으면
        given(userRepository.findByIdOrElseThrow(requestee.getId()))
            .willThrow(new CustomException(HttpStatus.BAD_REQUEST, "User Not Found"));

        //when & then : Bad Request 예외가 발생한다
        assertFailedValidation(user.getId(), requestee.getId(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("실패 - 신청 내역 조회 불가")
    void failedByFriendRequestNotFound() {
        //given : 내가 상대방에게 친구 신청한 내역이 없으면
        given(friendRequestRepository.existsByFromUserIdAndToUserId(user.getId(), requestee.getId()))
            .willReturn(false);

        given(userRepository.findByIdOrElseThrow(requestee.getId()))
            .willReturn(requestee);

        //when & then : Bad Request 예외가 발생한다
        assertFailedValidation(user.getId(), requestee.getId(), HttpStatus.BAD_REQUEST);
    }

    private void assertFailedValidation(Long requesterId, Long requesteeUserId, HttpStatus httpStatus) {
        assertThatThrownBy(() -> cancelFriendRequestService.doService(requesterId, requesteeUserId))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("httpStatus", httpStatus);
    }
}