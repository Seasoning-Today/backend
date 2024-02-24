package today.seasoning.seasoning.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi friendApi() {
        return GroupedOpenApi.builder()
                .group("친구 관련 API")
                .pathsToMatch("/friend/**")
                .build();
    }

    @Bean
    public GroupedOpenApi articleApi() {
        return GroupedOpenApi.builder()
                .group("게시물 관련 API")
                .pathsToMatch("/article/**")
                .build();
    }

    @Bean
    public GroupedOpenApi solartermApi() {
        return GroupedOpenApi.builder()
                .group("절기 관련 API")
                .pathsToMatch("/solarTerm/**")
                .build();
    }

    @Bean
    public GroupedOpenApi notificationApi() {
        return GroupedOpenApi.builder()
                .group("알림 관련 API")
                .pathsToMatch("/notification/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("사용자 관련 API")
                .pathsToMatch("/user/**")
                .build();
    }

    @Bean
    public OpenAPI springOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Seasoning API")
                        .description("시즈닝 API 명세서입니다.")
                        .version("v0.0.1"));

    }
}