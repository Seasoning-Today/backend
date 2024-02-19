package today.seasoning.seasoning.solarterm.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import today.seasoning.seasoning.BaseIntegrationTest;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.solarterm.domain.SolarTerm;
import today.seasoning.seasoning.solarterm.domain.SolarTermRepository;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@DisplayName("절기 등록 통합 테스트")
public class RegisterSolarTermIntegrationTest extends BaseIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Autowired
    SolarTermRepository solarTermRepository;

    static final String url = "/admin/solarTerm";

    @Test
    @DisplayName("성공 - 관리자 요청")
    @Sql(scripts = "classpath:data/insert_admin_user.sql")
    void test() throws Exception {
        //given
        User admin = userRepository.findByIdOrElseThrow(1L);

        //when
        int year = LocalDate.now().getYear();
        HashMap<String, Object> params = new HashMap<>();
        params.put("year", year);
        ExtractableResponse<Response> response = post(url, admin.getId(), params);

        //then
        List<SolarTerm> solarTerms = solarTermRepository.findAllByOrderByDateAsc();

        assertThat(response.statusCode())
            .as("상태 코드는 200이어야 한다")
            .isEqualTo(200);

        assertThat(solarTerms.size())
            .as("24개의 절기가 등록되어야 한다")
            .isEqualTo(24);

        checkMonth(solarTerms);
        checkSequence(solarTerms);
        checkYear(solarTerms, year);
    }

    private void checkMonth(List<SolarTerm> solarTerms) {
        for (int sequence = 1; sequence <= 24; sequence++) {
            LocalDate date = getDateBySequence(solarTerms, sequence);
            int expectedMonth = sequence < 23? (sequence + 3) / 2 : 1;

            assertThat(date.getMonthValue())
                .as(String.format("%d번째 절기는 %d월이어야 한다", sequence, expectedMonth))
                .isEqualTo(expectedMonth);
        }
    }

    private void checkSequence(List<SolarTerm> solarTerms) {
        for (int index = 0; index < 24; index++) {
            int expectedSequence = index + 1;

            assertThat(solarTerms.get(index).getSequence())
                .as(String.format("%d번째 날짜의 절기 순번은 %d이어야 한다", index + 1, expectedSequence))
                .isEqualTo(expectedSequence);
        }
    }

    private void checkYear(List<SolarTerm> solarTerms, int year) {
        for (int sequence = 1; sequence <= 24; sequence++) {
            LocalDate date = getDateBySequence(solarTerms, sequence);
            int expectedYear = sequence < 23 ? year : year + 1;

            assertThat(date.getYear())
                .as(String.format("%d번째 절기의 연도는 %d이어야 한다", sequence, expectedYear))
                .isEqualTo(expectedYear);
        }
    }

    private LocalDate getDateBySequence(List<SolarTerm> solarTerms, int sequence) {
        return solarTerms.stream()
            .filter(solarTerm -> solarTerm.getSequence() == sequence)
            .findFirst()
            .map(SolarTerm::getDate)
            .orElse(null);
    }

    @Test
    @DisplayName("실패 - 일반 회원 요청")
    void test2() throws Exception {
        //given
        User user = new User("nickname", "http://test.org/a.jpg", "email@test.org", LoginType.KAKAO);
        userRepository.save(user);

        //when
        int year = LocalDate.now().getYear();
        HashMap<String, Object> params = new HashMap<>();
        params.put("year", year);
        ExtractableResponse<Response> response = post(url, user.getId(), params);

        //then
        assertThat(response.statusCode())
            .as("상태 코드는 403이어야 한다")
            .isEqualTo(403);
    }

    @Test
    @DisplayName("실패 - 매니저 요청")
    @Sql(scripts = "classpath:data/insert_manager_user.sql")
    void test3() throws Exception {
        //given
        User manager = userRepository.findByIdOrElseThrow(1L);

        //when
        int year = LocalDate.now().getYear();
        HashMap<String, Object> params = new HashMap<>();
        params.put("year", year);
        ExtractableResponse<Response> response = post(url, manager.getId(), params);

        //then
        assertThat(response.statusCode())
            .as("상태 코드는 403이어야 한다")
            .isEqualTo(403);
    }
}
