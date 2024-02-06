package today.seasoning.seasoning.friendship.service;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;
import today.seasoning.seasoning.friendship.dto.FindUserFriendsResponse;
import today.seasoning.seasoning.user.domain.User;

@DisplayName("친구 목록 조회 서비스")
@ExtendWith(MockitoExtension.class)
class FindAllFriendsServiceTest {

    @Mock
    FriendshipRepository friendshipRepository;
    @InjectMocks
    FindAllFriendsService findAllFriendsService;

    @Test
    @DisplayName("성공")
    void success() {
        //given
        User user = new User("user", "https://test/user.jpg", "user@email.com", LoginType.KAKAO);
        User friend1 = new User("friend1", "https://test/friend1.jpg", "friend1@email.com", LoginType.KAKAO);
        User friend2 = new User("friend2", "https://test/friend2.jpg", "friend2@email.com", LoginType.KAKAO);
        User friend3 = new User("friend3", "https://test/friend3.jpg", "friend3@email.com", LoginType.KAKAO);

        List<FindUserFriendsResponse> expectedResult = List.of(
            new FindUserFriendsResponse(TsidUtil.toString(friend1.getId()), friend1.getNickname(), friend1.getAccountId(), friend1.getProfileImageUrl()),
            new FindUserFriendsResponse(TsidUtil.toString(friend2.getId()), friend2.getNickname(), friend2.getAccountId(), friend2.getProfileImageUrl()),
            new FindUserFriendsResponse(TsidUtil.toString(friend3.getId()), friend3.getNickname(), friend3.getAccountId(), friend3.getProfileImageUrl())
        );

        BDDMockito.given(friendshipRepository.findFriendsByUserId(user.getId()))
            .willReturn(List.of(friend1, friend2, friend3));

        //when
        List<FindUserFriendsResponse> actualResult = findAllFriendsService.doService(user.getId());

        //then
        Assertions.assertThat(actualResult)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedResult);
    }
}