package today.seasoning.seasoning.notice.integration;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.LinkedList;
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
import today.seasoning.seasoning.notice.domain.Notice;
import today.seasoning.seasoning.notice.domain.NoticeRepository;
import today.seasoning.seasoning.notice.dto.NoticeResponse;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@DisplayName("공지사항 조회 통합 테스트")
public class FindNoticeIntegrationTest extends BaseIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    NoticeRepository noticeRepository;

    @InjectSoftAssertions
    SoftAssertions softAssertions;

    final String url = "/notice";

    User user;
    List<Notice> notices;

    @BeforeEach
    void initData() {
        user = new User("nickname", "https://test.org/a.jpg", "email@test.org", LoginType.KAKAO);
        userRepository.save(user);

        notices = new LinkedList<>();
        for (int i = 1; i <= 15; i++) {
            Notice notice = new Notice(String.valueOf(i));
            noticeRepository.save(notice);
            notices.add(notice);
        }
    }

    @Test
    @DisplayName("첫번째 페이지 조회")
    void test() {
        //when : 전체 공지사항의 개수가 15개일 때, 첫번째 페이지 조회 시
        ExtractableResponse<Response> response = get(url, user.getId());

        List<NoticeResponse> noticeResponses = response.as(new TypeRef<>() {
        });

        //then
        softAssertions.assertThat(response.statusCode())
            .as("상태 코드는 200이어야 한다")
            .isEqualTo(200);

        softAssertions.assertThat(noticeResponses.size())
            .as("조회된 공지사항의 개수는 10개이다")
            .isEqualTo(10);

        List<String> contents = noticeResponses.stream()
            .map(NoticeResponse::getContent)
            .collect(Collectors.toList());

        softAssertions.assertThat(contents)
            .as("조회된 공지사항은 최신순으로 정렬되어 있어야 한다")
            .usingRecursiveComparison()
            .isEqualTo(List.of("15", "14", "13", "12", "11", "10", "9", "8", "7", "6"));
    }

    @Test
    @DisplayName("성공 - 두번째 페이지 조회")
    void test2() {
        //given : lastNoticeId - 첫번째 페이지의 마지막 공지사항의 아이디
        Long lastNoticeId = notices.get(5).getId();

        //when : 전체 공지사항의 개수가 15개일 때, 두번째 페이지 조회 시
        HashMap<String, Object> params = new HashMap<>();
        params.put("lastId", TsidUtil.toString(lastNoticeId));

        ExtractableResponse<Response> response = get(url, user.getId(), params);

        List<NoticeResponse> noticeResponses = response.as(new TypeRef<>() {
        });

        //then
        softAssertions.assertThat(response.statusCode())
            .as("상태 코드는 200이어야 한다")
            .isEqualTo(200);

        softAssertions.assertThat(noticeResponses.size())
            .as("조회된 공지사항의 개수는 5개이다")
            .isEqualTo(5);

        List<String> contents = noticeResponses.stream()
            .map(NoticeResponse::getContent)
            .collect(Collectors.toList());

        softAssertions.assertThat(contents)
            .as("공지사항은 최신순으로 정렬되어 있어야 한다")
            .usingRecursiveComparison()
            .isEqualTo(List.of("5", "4", "3", "2", "1"));
    }
}
