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

@DisplayName("공지사항 수정 통합 테스트")
public class UpdateNoticeIntegrationTest extends BaseIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    NoticeRepository noticeRepository;

    @InjectSoftAssertions
    SoftAssertions softAssertions;

    final String url = "/notice";

    Long noticeId;

    @BeforeEach
    void initNotice() {
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
        String content = "updated by admin";

        //when : 관리자가 공지사항 수정 요청 시
        Map<String, Object> params = createRequestParameters();
        ExtractableResponse<Response> response = put(url, admin.getId(), params, content);

        //then : 요청은 성공해야 한다
        assertSuccessfulResult(response, content);
    }

    @Test
    @Sql(scripts = "classpath:data/insert_manager_user.sql")
    @DisplayName("성공 - 매니저 요청")
    void requestByManager() {
        //given
        User manager = userRepository.findByIdOrElseThrow(1L);
        String content = "updated by manager";

        //when : 매니저가 공지사항 수정 요청 시
        Map<String, Object> params = createRequestParameters();
        ExtractableResponse<Response> response = put(url, manager.getId(), params, content);

        //then : 요청은 성공해야 한다
        assertSuccessfulResult(response, content);
    }

    private void assertSuccessfulResult(ExtractableResponse<Response> response, String content) {
        softAssertions.assertThat(response.statusCode())
            .as("상태 코드는 200이어야 한다")
            .isEqualTo(200);

        Notice notice = noticeRepository.findByIdOrElseThrow(noticeId);
        softAssertions.assertThat(notice.getContent())
            .as("공지사항은 요청대로 변경되어야 한다")
            .isEqualTo(content);
    }

    @Test
    @DisplayName("실패 - 일반 회원 요청")
    void requestByUser() {
        //given
        User user = new User("nickname", "http://test.org/a.jpg", "email@test.org", LoginType.KAKAO);
        userRepository.save(user);

        String content = "updated by user";

        //when : 일반 회원이 공지사항 수정 요청 시
        Map<String, Object> params = createRequestParameters();
        ExtractableResponse<Response> response = put(url, user.getId(), params, content);

        //then : 요청은 실패해야 한다
        softAssertions.assertThat(response.statusCode())
            .as("상태 코드는 403이어야 한다")
            .isEqualTo(403);

        Notice notice = noticeRepository.findByIdOrElseThrow(noticeId);
        softAssertions.assertThat(notice.getContent())
            .as("공지사항은 변경되지 않아야 한다")
            .isNotEqualTo(content);
    }

    private Map<String, Object> createRequestParameters() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", TsidUtil.toString(noticeId));
        return params;
    }
}
