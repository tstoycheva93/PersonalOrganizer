package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PersonalOrganizerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalOrganizerApplication.class, args);
    }

}
