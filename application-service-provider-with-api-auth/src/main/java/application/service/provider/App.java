package application.service.provider;

import application.service.provider.entity.User;
import application.service.provider.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    ApplicationRunner init(UserRepository repository) {
        return args -> {
            User user1 = new User(1L, "account1", "Keven", 20, new BigDecimal("100.00"));
            User user2 = new User(2L, "account2", "Logan", 28, new BigDecimal("180.00"));
            User user3 = new User(3L, "account3", "John", 32, new BigDecimal("280.00"));
            Stream.of(user1, user2, user3)
                    .forEach(repository::save);
        };
    }
}
