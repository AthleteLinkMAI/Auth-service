package team.anonyms.athletelink.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main class is the main entrypoint of the auth microservice.
 */
@SpringBootApplication
@EntityScan(basePackages = {"team.anonyms.athletelink.authservice.entity"})
@EnableJpaRepositories(basePackages = {"team.anonyms.athletelink.authservice.repository"})
public class Main {
    static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
