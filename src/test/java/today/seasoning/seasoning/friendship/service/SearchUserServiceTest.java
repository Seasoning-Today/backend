package today.seasoning.seasoning.friendship.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import today.seasoning.seasoning.common.enums.FriendshipStatus;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.friendship.domain.FriendRequestRepository;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;
import today.seasoning.seasoning.friendship.dto.SearchUserResult;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@DisplayName("회원 검색 서비스")
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
class SearchUserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    FriendshipRepository friendshipRepository;
    @Mock
    FriendRequestRepository friendRequestRepository;
    @InjectMocks
    SearchUserService searchUserService;
    @InjectSoftAssertions
    SoftAssertions softAssertions;

    User user = new User("user", "https://test/user.jpg", "user@email.com", LoginType.KAKAO);
    User target = new User("target", "https://test/target.jpg", "target@email.com", LoginType.KAKAO);

    @Test
    @DisplayName("성공 - 본인 검색")
    void searchMySelf() {
        //given : 상대방이 본인이라면
        String targetAccountId = user.getAccountId();

        given(userRepository.findByAccountId(targetAccountId))
            .willReturn(Optional.of(user));

         // when : 회원 검색 시
        SearchUserResult result = searchUserService.doService(user.getId(), targetAccountId);

        //then: 본인의 프로필 정보와 SELF를 반환해야 한다
        assertSearchUserResult(result, user, FriendshipStatus.SELF);
    }

    @Test
    @DisplayName("성공 - 친구 검색")
    void searchFriend() {
        //given : 상대방이 친구라면
        given(friendshipRepository.existsByUserIdAndFriendId(user.getId(), target.getId()))
            .willReturn(true);

        given(userRepository.findByAccountId(target.getAccountId()))
            .willReturn(Optional.of(target));


        //when : 회원 검색 시
        SearchUserResult result = searchUserService.doService(user.getId(), target.getAccountId());

        //then: 친구의 프로필 정보와 FRIEND를 반환해야 한다
        assertSearchUserResult(result, target, FriendshipStatus.FRIEND);
    }

    @Test
    @DisplayName("성공 - 나에게 친구 신청한 사용자 검색")
    void testReceived() {
        //given: 상대방이 나에게 친구 신청한 상태라면
        given(friendRequestRepository.existsByFromUserIdAndToUserId(target.getId(), user.getId()))
            .willReturn(true);

        given(friendRequestRepository.existsByFromUserIdAndToUserId(user.getId(), target.getId()))
            .willReturn(false);

        given(userRepository.findByAccountId(target.getAccountId()))
            .willReturn(Optional.of(target));

        //when : 회원 검색 시
        SearchUserResult result = searchUserService.doService(user.getId(), target.getAccountId());

        //then: 사용자의 프로필 정보와 RECEIVED를 반환해야 한다
        assertSearchUserResult(result, target, FriendshipStatus.RECEIVED);
    }

    @Test
    @DisplayName("성공 - 서로 친구 요청한 상태")
    void testReceived2() {
        //given: 서로 친구 요청한 상태라면
        given(friendRequestRepository.existsByFromUserIdAndToUserId(user.getId(), target.getId()))
            .willReturn(true);

        given(friendRequestRepository.existsByFromUserIdAndToUserId(target.getId(), user.getId()))
            .willReturn(true);

        given(userRepository.findByAccountId(target.getAccountId()))
            .willReturn(Optional.of(target));

        //when : 회원 검색 시
        SearchUserResult result = searchUserService.doService(user.getId(), target.getAccountId());

        //then: 사용자의 프로필 정보와 RECEIVED를 반환해야 한다
        assertSearchUserResult(result, target, FriendshipStatus.RECEIVED);
    }

    @Test
    @DisplayName("성공 - 내가 친구 요청한 사용자 검색")
    void testSent() {
        //given: 내가 상대방에게 친구 요청한 상태라면
        given(friendRequestRepository.existsByFromUserIdAndToUserId(user.getId(), target.getId()))
            .willReturn(true);

        given(friendRequestRepository.existsByFromUserIdAndToUserId(target.getId(), user.getId()))
            .willReturn(false);

        given(userRepository.findByAccountId(target.getAccountId()))
            .willReturn(Optional.of(target));

        //when : 회원 검색 시
        SearchUserResult result = searchUserService.doService(user.getId(), target.getAccountId());

        //then: 사용자의 프로필 정보와 SENT를 반환해야 한다
        assertSearchUserResult(result, target, FriendshipStatus.SENT);
    }

    private void assertSearchUserResult(SearchUserResult result, User target, FriendshipStatus friendshipStatus) {
        softAssertions.assertThat(TsidUtil.toLong(result.getId())).isEqualTo(target.getId());
        softAssertions.assertThat(result.getNickname()).isEqualTo(target.getNickname());
        softAssertions.assertThat(result.getImage()).isEqualTo(target.getProfileImageUrl());
        softAssertions.assertThat(result.getFriendshipStatus()).isEqualTo(friendshipStatus);
    }

    @Test
    @DisplayName("실패 - 아이디 조회 불가")
    void failedByFriendNotFound() {
        //given : 아이디와 일치하는 사용자가 없으면
        given(userRepository.findByAccountId(anyString()))
            .willReturn(Optional.empty());

        //when & then : Bad Request 예외가 발생한다
        assertThatThrownBy(() -> searchUserService.doService(user.getId(), "targetAccountId"))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
    }
}
