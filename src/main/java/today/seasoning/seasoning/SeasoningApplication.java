package today.seasoning.seasoning;

import java.time.LocalDateTime;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class SeasoningApplication {

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

    public static void main(String[] args) {
        SpringApplication.run(SeasoningApplication.class, args);
    }

    @PostConstruct
    void setTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        log.info("Server Time : {}", LocalDateTime.now());
    }

    @PostConstruct
    void logDatasourceUrl() {
        log.info("Datasource URL : {}", dataSourceUrl);
    }

}
