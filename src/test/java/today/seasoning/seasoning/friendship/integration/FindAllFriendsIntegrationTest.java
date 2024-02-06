package today.seasoning.seasoning.friendship.integration;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import today.seasoning.seasoning.BaseIntegrationTest;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.friendship.domain.Friendship;
import today.seasoning.seasoning.friendship.domain.FriendshipRepository;
import today.seasoning.seasoning.friendship.dto.FindUserFriendsResponse;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@DisplayName("친구 목록 조회 통합 테스트")
public class FindAllFriendsIntegrationTest extends BaseIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    FriendshipRepository friendshipRepository;

    @InjectSoftAssertions
    SoftAssertions softAssertions;

    // API 주소
    static String url = "/friend/list";

    // Mock 유저 정보
    List<User> users;

    @BeforeEach
    void initMockUsers() {
        //given
        users = List.of(
            new User("userNickname0", "https://test.org/user0.jpg", "user0@email.com", LoginType.KAKAO),
            new User("userNickname1", "https://test.org/user1.jpg", "user1@email.com", LoginType.KAKAO),
            new User("userNickname2", "https://test.org/user2.jpg", "user2@email.com", LoginType.KAKAO),
            new User("userNickname3", "https://test.org/user3.jpg", "user3@email.com", LoginType.KAKAO)
        );
        userRepository.saveAll(users);
    }

    @Test
    @DisplayName("모든 친구가 조회되어야 한다")
    void findAllFriends() {
        //given : 회원이 user1, user2, user3와 친구일 때
        User user = users.get(0);
        List<User> friends = users.subList(1, 4);

        setFriendships(user, friends);

        //when : 친구 목록을 조회하면
        ExtractableResponse<Response> response = get(url, user.getId());

        List<FindUserFriendsResponse> responseFriendList = response.body().as(new TypeRef<>() {
        });

        //then : 응답은 다음과 같아야 한다
        softAssertions.assertThat(response.statusCode())
            .as("상태 코드는 200이어야 한다")
            .isEqualTo(200);

        softAssertions.assertThat(responseFriendList.size())
            .as("조회된 친구의 수는 3명이다.")
            .isEqualTo(3);

        softAssertions.assertThat(responseFriendList)
            .as("조회된 친구의 정보는 실제 회원의 정보와 일치해야 한다")
            .usingRecursiveComparison()
            .isEqualTo(createFindUserFriendsResponse(friends));
    }

    @Test
    @DisplayName("친구가 아닌 회원은 조회되면 안된다")
    void findAllFriendsExcludingNonFriends() {
        //given : 회원이 user1과 user2와 친구이고, user3와는 친구가 아닐 때
        User user = users.get(0);
        List<User> friends = users.subList(1, 3);
        User nonFriendUser = users.get(3);

        setFriendships(user, friends);

        //when : 친구 목록을 조회하면
        ExtractableResponse<Response> response = get(url, user.getId());

        List<FindUserFriendsResponse> responseFriendList = response.body().as(new TypeRef<>() {
        });

        //then : 응답은 다음과 같아야 한다
        softAssertions.assertThat(response.statusCode())
            .as("상태 코드는 200이어야 한다")
            .isEqualTo(200);

        softAssertions.assertThat(responseFriendList.size())
            .as("조회된 친구의 수는 2명이다.")
            .isEqualTo(2);

        softAssertions.assertThat(responseFriendList)
            .as("조회된 친구의 정보는 실제 회원의 정보와 일치해야 한다")
            .usingRecursiveComparison()
            .isEqualTo(createFindUserFriendsResponse(friends));

        softAssertions.assertThat(responseFriendList)
            .as("친구가 아닌 회원의 정보는 조회되면 안된다")
            .usingRecursiveComparison()
            .isNotEqualTo(createFindUserFriendsResponse(nonFriendUser));
    }


    private void setFriendships(User user, List<User> friends) {
        friends.forEach(friend -> {
            friendshipRepository.save(new Friendship(user, friend));
            friendshipRepository.save(new Friendship(friend, user));
        });
    }

    private List<FindUserFriendsResponse> createFindUserFriendsResponse(List<User> users) {
        return users.stream()
            .map(this::createFindUserFriendsResponse)
            .collect(Collectors.toList());
    }

    private FindUserFriendsResponse createFindUserFriendsResponse(User user) {
        return new FindUserFriendsResponse(
            TsidUtil.toString(user.getId()),
            user.getNickname(),
            user.getAccountId(),
            user.getProfileImageUrl());
    }

}
