package today.seasoning.seasoning.notice.integration;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import today.seasoning.seasoning.BaseIntegrationTest;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.notice.domain.Notice;
import today.seasoning.seasoning.notice.domain.NoticeRepository;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

public class DeleteNoticeIntegrationTest extends BaseIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    NoticeRepository noticeRepository;

    @InjectSoftAssertions
    SoftAssertions softAssertions;

    final String url = "/notice";

    Long noticeId;

    @BeforeEach
    void registerNotice() {
        Notice notice = new Notice("initial status");
        noticeId = notice.getId();
        noticeRepository.save(notice);
    }

    @Test
    @Sql(scripts = "classpath:data/insert_admin_user.sql")
    @DisplayName("성공 - 관리자 요청")
    void requestByAdmin() {
        //given
        User admin = userRepository.findByIdOrElseThrow(1L);

        //when : 관리자가 공지사항 삭제 요청 시
        Map<String, Object> params = createRequestParameters();
        ExtractableResponse<Response> response = delete(url, admin.getId(), params);

        //then : 요청은 성공해야 한다
        assertSuccessfulResult(response);
    }

    @Test
    @Sql(scripts = "classpath:data/insert_manager_user.sql")
    @DisplayName("성공 - 매니저 요청")
    void requestByManager() {
        //given
        User manager = userRepository.findByIdOrElseThrow(1L);

        //when : 매니저가 공지사항 삭제 요청 시
        Map<String, Object> params = createRequestParameters();
        ExtractableResponse<Response> response = delete(url, manager.getId(), params);

        //then : 요청은 성공해야 한다
        assertSuccessfulResult(response);
    }

    private void assertSuccessfulResult(ExtractableResponse<Response> response) {
        softAssertions.assertThat(response.statusCode())
            .as("상태 코드는 200이어야 한다")
            .isEqualTo(200);

        softAssertions.assertThat(noticeRepository.count())
            .as("공지사항은 삭제되어야 한다")
            .isEqualTo(0);
    }

    @Test
    @DisplayName("실패 - 일반 회원 요청")
    void requestByUser() {
        //given
        User user = new User("nickname", "http://test.org/a.jpg", "email@test.org", LoginType.KAKAO);
        userRepository.save(user);

        //when : 일반 회원이 공지사항 삭제 요청 시
        Map<String, Object> params = createRequestParameters();
        ExtractableResponse<Response> response = delete(url, user.getId(), params);

        //then : 요청은 실패해야 한다
        softAssertions.assertThat(response.statusCode())
            .as("상태 코드는 403이어야 한다")
            .isEqualTo(403);

        softAssertions.assertThat(noticeRepository.findById(noticeId))
            .as("공지사항은 삭제되지 않아야 한다")
            .isPresent();
    }

    private Map<String, Object> createRequestParameters() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", TsidUtil.toString(noticeId));
        return params;
    }
}
