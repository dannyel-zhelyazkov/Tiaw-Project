package dny.apps.tiaw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TiawApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiawApplication.class, args);
	}
}
