package today.seasoning.seasoning;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.FileCopyUtils;
import today.seasoning.seasoning.common.util.JwtUtil;

@ActiveProfiles("test")
@ExtendWith(SoftAssertionsExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BaseIntegrationTest {

    static {
        System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @LocalServerPort
    int port;

    @BeforeEach
    void init() {
        // 실제 서블릿 컨테이너 실행을 위한 RANDOM PORT 설정
        RestAssured.port = port;

        // 모든 테이블 데이터 초기화 (정적 데이터 fortune, solar_term 제외)
        Resource resource = new ClassPathResource("/data/clear.sql");
        try {
            String sql = FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            Arrays.stream(sql.split("\n")).forEach(jdbcTemplate::execute);
        } catch (IOException e) {
            throw new RuntimeException("Error reading or executing SQL script: /data/clear.sql", e);
        }
    }

    public ExtractableResponse<Response> post(String url, Long userId, JSONObject jsonBody) {
        RequestSpecification request = RestAssured
            .given().log().all()
                .contentType("application/json")
                .header("Authorization", "Bearer " + createAccessToken(userId));

        if (jsonBody != null) {
            request.body(jsonBody.toString());
        }

        return request
            .when().post(url)
            .then().log().all().extract();
    }

    public ExtractableResponse<Response> get(String url, Long userId) {
        return get(url, userId, new HashMap<>());
    }


    public ExtractableResponse<Response> get(String url, Long userId, Map<String, Object> params) {
        return RestAssured
            .given().log().all()
                .header("Authorization", "Bearer " + createAccessToken(userId))
                .params(params)
            .when().get(url)
            .then().log().all().extract();
    }

    private String createAccessToken(Long userId) {
        return JwtUtil.createToken(userId).getAccessToken();
    }
}
