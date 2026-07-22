package team.anonyms.athletelink.authorisation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main class is the main entrypoint of the authorisation microservice.
 */
@SpringBootApplication
@EntityScan(basePackages = {"team.anonyms.athletelink.authorisation.entities"})
@EnableJpaRepositories(basePackages = {"team.anonyms.athletelink.authorisation.repositories"})
public class Main {
    static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
