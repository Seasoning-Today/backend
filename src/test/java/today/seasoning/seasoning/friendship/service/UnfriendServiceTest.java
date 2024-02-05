package today.seasoning.seasoning.friendship.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.friendship.domain.Friendship;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@DisplayName("친구 삭제 서비스")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UnfriendServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    FriendshipRepository friendshipRepository;
    @InjectMocks
    UnfriendService unfriendService;

    User user = new User("user", "https://test/user.jpg", "user@email.com", LoginType.KAKAO);
    User friend = new User("friend", "https://test/friend.jpg", "friend@email.com", LoginType.KAKAO);
    Friendship userToFriendFriendship = new Friendship(user, friend);
    Friendship friendToUserFriendship = new Friendship(friend, user);

    @Test
    @DisplayName("성공")
    void test() {
        //given : 아이디에 해당하는 사용자가 존재하고, 해당 사용자와 회원간의 친구 관계가 양방향으로 존재하는 경우
        BDDMockito.given(userRepository.findById(friend.getId()))
            .willReturn(Optional.of(friend));

        BDDMockito.given(friendshipRepository.findByUserIdAndFriendId(user.getId(), friend.getId()))
            .willReturn(Optional.of(userToFriendFriendship));

        BDDMockito.given(friendshipRepository.findByUserIdAndFriendId(friend.getId(), user.getId()))
            .willReturn(Optional.of(friendToUserFriendship));

        //when && then : 예외가 발생하지 않는다
        Assertions.assertDoesNotThrow(() -> unfriendService.doService(user.getId(), friend.getId()));
    }

    @Test
    @DisplayName("실패 - 상대방 조회 불가")
    void failedByFriendNotFound() {
        //given : 아이디에 해당하는 사용자가 존재하지 않으면
        given(userRepository.findById(friend.getId()))
            .willReturn(Optional.empty());

        //when & then : Bad Request 예외가 발생한다
        assertFailedValidation(user.getId(), friend.getId(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("실패 - 관계 조회 실패(회원 -> 친구)")
    void failedByFriendshipNotFoundFromUserToFriend() {
        //given : 회원으로부터 친구로의 친구 관계가 존재하지 않으면
        BDDMockito.given(friendshipRepository.findByUserIdAndFriendId(user.getId(), friend.getId()))
            .willReturn(Optional.empty());

        BDDMockito.given(friendshipRepository.findByUserIdAndFriendId(friend.getId(), user.getId()))
            .willReturn(Optional.of(friendToUserFriendship));

        BDDMockito.given(userRepository.findById(friend.getId()))
            .willReturn(Optional.of(friend));

        //when & then : Bad Request 예외가 발생한다
        assertFailedValidation(user.getId(), friend.getId(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("실패 - 관계 조회 실패(친구 -> 회원)")
    void failedByFriendshipNotFoundFromFriendToUser() {
        //given : 친구로부터 회원으로의 친구 관계가 존재하지 않으면
        BDDMockito.given(friendshipRepository.findByUserIdAndFriendId(friend.getId(), user.getId()))
            .willReturn(Optional.empty());

        BDDMockito.given(friendshipRepository.findByUserIdAndFriendId(user.getId(), friend.getId()))
            .willReturn(Optional.of(userToFriendFriendship));

        BDDMockito.given(userRepository.findById(friend.getId()))
            .willReturn(Optional.of(friend));

        //when & then : Bad Request 예외가 발생한다
        assertFailedValidation(user.getId(), friend.getId(), HttpStatus.BAD_REQUEST);
    }

    private void assertFailedValidation(Long userId, Long friendUserId, HttpStatus httpStatus) {
        assertThatThrownBy(() -> unfriendService.doService(userId, friendUserId))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("httpStatus", httpStatus);
    }
}
