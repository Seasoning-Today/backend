package today.seasoning.seasoning.friendship.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;

@DisplayName("친구 여부 확인 서비스")
@ExtendWith(MockitoExtension.class)
class CheckFriendshipServiceTest {

    FriendshipRepository friendshipRepository;
    CheckFriendshipService checkFriendshipService;

    @BeforeEach
    void initMocks() {
        friendshipRepository = mock(FriendshipRepository.class);
        checkFriendshipService = new CheckFriendshipServiceImpl(friendshipRepository);
    }

    @Test
    @DisplayName("조회 성공 시, true를 반환한다")
    void trueIfFound() {
        //given
        given(friendshipRepository.existsByUserIdAndFriendId(anyLong(), anyLong()))
            .willReturn(true);

        //when
        boolean result = checkFriendshipService.doCheck(1L, 2L);

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("조회 실패 시, false를 반환한다")
    void falseIfNotFound() {
        //given
        given(friendshipRepository.existsByUserIdAndFriendId(anyLong(), anyLong()))
            .willReturn(false);

        //when
        boolean result = checkFriendshipService.doCheck(1L, 2L);

        //then
        assertThat(result).isFalse();
    }

}