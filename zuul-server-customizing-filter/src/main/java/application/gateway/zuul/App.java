package application.gateway.zuul;

import application.gateway.zuul.filter.PreLogFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

/**
 * The @EnableZuulProxy integrates Ribbon and Hystrix
 */
@Slf4j
@SpringBootApplication
@EnableZuulProxy
public class App {
    @Bean
    public PreLogFilter preLogFilter() {
        return new PreLogFilter();
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
