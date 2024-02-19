package today.seasoning.seasoning.notice.integration;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import today.seasoning.seasoning.BaseIntegrationTest;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.notice.domain.Notice;
import today.seasoning.seasoning.notice.domain.NoticeRepository;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@DisplayName("공지사항 등록 통합 테스트")
public class RegisterNoticeIntegrationTest extends BaseIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    NoticeRepository noticeRepository;

    @InjectSoftAssertions
    SoftAssertions softAssertions;

    final String url = "/notice";
    final String content = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.";

    @Test
    @Sql(scripts = "classpath:data/insert_admin_user.sql")
    @DisplayName("성공 - 관리자 요청")
    void requestByAdmin() {
        //given
        User admin = userRepository.findByIdOrElseThrow(1L);

        //when : 관리자가 공지사항 등록 요청 시
        ExtractableResponse<Response> response = post(url, admin.getId(), content);

        //then : 요청은 성공해야 한다
        softAssertions.assertThat(response.statusCode())
            .as("상태 코드는 200이어야 한다")
            .isEqualTo(200);

        assertNoticeRegistration();
    }

    @Test
    @Sql(scripts = "classpath:data/insert_manager_user.sql")
    @DisplayName("성공 - 매니저 요청")
    void requestByManager() {
        User manager = userRepository.findByIdOrElseThrow(1L);

        //when : 매니저가 공지사항 등록 요청 시
        ExtractableResponse<Response> response = post(url, manager.getId(), content);

        //then : 요청은 성공해야 한다
        softAssertions.assertThat(response.statusCode())
            .as("상태 코드는 200이어야 한다")
            .isEqualTo(200);

        assertNoticeRegistration();
    }

    private void assertNoticeRegistration() {
        List<Notice> notices = noticeRepository.findAll();

        softAssertions.assertThat(notices.size())
            .as("등록된 공지사항은 1개이어야 한다")
            .isEqualTo(1);

        softAssertions.assertThat(notices.get(0).getContent())
            .as("공지사항의 내용은 동일해야 한다")
            .isEqualTo(content);
    }

    @Test
    @DisplayName("실패 - 일반 회원 요청")
    void requestByUser() {
        //given
        User user = new User("nickname", "https://test.org/a.jpg", "user@test.org", LoginType.KAKAO);
        userRepository.save(user);

        //when : 일반 회원이 공지사항 등록 요청 시
        ExtractableResponse<Response> response = post(url, user.getId(), content);

        //then : 요청은 실패해야 한다
        softAssertions.assertThat(response.statusCode())
            .as("상태코드는 403이어야 한다")
            .isEqualTo(403);

        softAssertions.assertThat(noticeRepository.count())
            .as("등록된 공지사항은 없어야 한다")
            .isEqualTo(0);
    }

}
