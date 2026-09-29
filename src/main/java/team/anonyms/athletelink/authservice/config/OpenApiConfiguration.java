package team.anonyms.athletelink.authservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "AtheleteLink Auth Service",
                version = "0.0.1",
                description = "Authentication, verification, session and user management",
                license = @License(name = "MIT License")
        )
)
@Configuration
public class OpenApiConfiguration {
}
