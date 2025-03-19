package pl.edu.agh.io_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IoProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(IoProjectApplication.class, args);
	}

}
