package today.seasoning.seasoning.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "localDateTimeProvider")
public class JpaAuditingConfig {

}
