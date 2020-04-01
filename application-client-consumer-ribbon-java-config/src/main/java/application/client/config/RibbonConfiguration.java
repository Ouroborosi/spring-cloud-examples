package application.client.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ribbon Configuration Class
 * !! Beware: This class has to be a @Configuration class and should not in a @ComponentScan for the main application context.
 */
@Configuration
public class RibbonConfiguration {
    @Bean
    public IRule ribbonRule() {
        // define ribbon load balancer rule as RandomRule
        return new RandomRule();
    }
}