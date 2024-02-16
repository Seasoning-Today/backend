package today.seasoning.seasoning.friendship.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.BeforeEach;
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
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@DisplayName("친구 신청 서비스")
@ExtendWith(MockitoExtension.class)
class SendFriendRequestServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    FriendRequestRepository friendRequestRepository;
    @Mock
    FriendshipRepository friendshipRepository;
    @Mock
    ApplicationEventPublisher applicationEventPublisher;
    @InjectMocks
    SendFriendRequestService sendFriendRequestService;

    User requester = new User("requester", "https://test.com/requester.jpg", "requester@email.com", LoginType.KAKAO);
    User requestee = new User("requestee", "https://test.com/requestee.jpg", "requestee@email.com", LoginType.KAKAO);

    @BeforeEach
    void initUserRepository() {
        given(userRepository.findByIdOrElseThrow(requester.getId()))
            .willReturn(requester);
    }

    @Test
    @DisplayName("성공")
    void success() {
        //given : 상대방이 존재하고, 친구 신청 내역이 없으면
        given(userRepository.findByIdOrElseThrow(requestee.getId()))
            .willReturn(requestee);

        given(friendRequestRepository.existsByFromUserIdAndToUserId(requester.getId(), requestee.getId()))
            .willReturn(false);

        //when & then : 예외가 발생하지 않는다
        assertDoesNotThrow(() -> sendFriendRequestService.doService(requester.getId(), requestee.getId()));
    }

    @Test
    @DisplayName("실패 - 상대방 조회 실패")
    void failedByRequesteeNotFound() {
        //given : 아이디에 해당하는 회원이 없는 경우
        given(userRepository.findByIdOrElseThrow(requestee.getId()))
            .willThrow(new CustomException(HttpStatus.BAD_REQUEST, "User Not Found"));

        //when & then : Bad Request 예외가 발생한다
        assertFailedValidation(requester.getId(), requestee.getId(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("실패 - 자신에게 친구 신청")
    void failedBySelfRequest() {
        //given : 자신의 아이디로 친구 신청한 경우
        Long userId = requester.getId();

        given(userRepository.findByIdOrElseThrow(userId))
            .willReturn(requester);

        //when & then : Bad Request 예외가 발생한다
        assertFailedValidation(requester.getId(), userId, HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("실패 - 이미 신청한 상태")
    void failedByAlreadyExists() {
        //given : 친구 신청 내역이 존재하는 경우(=이미 신청한 경우)
        given(friendRequestRepository.existsByFromUserIdAndToUserId(requester.getId(), requestee.getId()))
            .willReturn(true);

        given(userRepository.findByIdOrElseThrow(requestee.getId()))
            .willReturn(requestee);

        //when & then : 409 Conflict 예외가 발생한다
        assertFailedValidation(requester.getId(), requestee.getId(), HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("실패 - 이미 친구인 상태")
    void test() {
        //given: 이미 친구인 경우
        given(friendshipRepository.existsByUserIdAndFriendId(requester.getId(), requestee.getId()))
            .willReturn(true);

        given(userRepository.findByIdOrElseThrow(requestee.getId()))
            .willReturn(requestee);

        given(friendRequestRepository.existsByFromUserIdAndToUserId(requester.getId(), requestee.getId()))
            .willReturn(false);

        //when & then : 409 Conflict 예외가 발생한다
        assertFailedValidation(requester.getId(), requestee.getId(), HttpStatus.CONFLICT);
    }

    private void assertFailedValidation(Long requesterId, Long requesteeUserId, HttpStatus httpStatus) {
        assertThatThrownBy(() -> sendFriendRequestService.doService(requesterId, requesteeUserId))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("httpStatus", httpStatus);
    }
}
