package today.seasoning.seasoning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SeasoningApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeasoningApplication.class, args);
	}

}
