package today.seasoning.seasoning;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import today.seasoning.seasoning.common.util.JwtUtil;

@ActiveProfiles("test")
@ExtendWith(SoftAssertionsExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BaseIntegrationTest {

    static {
        System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
    }

    @Autowired
    DataSource dataSource;

    @LocalServerPort
    private int port;

    @BeforeAll
    protected static void init(@Autowired DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/clear.sql"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    protected void initPort() {
        // 실제 서블릿 컨테이너 실행을 위한 RANDOM PORT 설정
        RestAssured.port = port;
    }

    @AfterEach
    protected void cleanData() {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/clear.sql"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected ExtractableResponse<Response> get(String url, Long userId) {
        return get(url, userId, new HashMap<>());
    }

    protected ExtractableResponse<Response> get(String url, Long userId, Map<String, Object> params) {
        return RestAssured
            .given().log().all()
            .header("Authorization", "Bearer " + createAccessToken(userId))
            .params(params)
            .when().get(url)
            .then().log().all().extract();
    }

    protected ExtractableResponse<Response> post(String url, Long userId, JSONObject jsonBody) {
        return RestAssured
            .given().log().all()
            .contentType("application/json")
            .header("Authorization", "Bearer " + createAccessToken(userId))
            .body(jsonBody == null ? "" : jsonBody.toString())
            .when().post(url)
            .then().log().all().extract();
    }

    protected ExtractableResponse<Response> post(String url, Long userId, String body) {
        return RestAssured
            .given().log().all()
            .contentType("application/json")
            .header("Authorization", "Bearer " + createAccessToken(userId))
            .body(body)
            .when().post(url)
            .then().log().all().extract();
    }

    protected ExtractableResponse<Response> post(String url, Long userId, Map<String, Object> params) {
        return RestAssured
            .given().log().all()
            .header("Authorization", "Bearer " + createAccessToken(userId))
            .params(params == null ? new HashMap<>() : params)
            .when().post(url)
            .then().log().all().extract();
    }

    protected String createAccessToken(Long userId) {
        return JwtUtil.createToken(userId).getAccessToken();
    }
}
