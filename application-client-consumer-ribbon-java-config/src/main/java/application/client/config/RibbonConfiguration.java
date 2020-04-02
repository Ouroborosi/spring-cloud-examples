package application.client.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;

/**
 * Ribbon Configuration Class
 */
public class RibbonConfiguration {
    @Bean
    public IRule ribbonRule() {
        // define ribbon load balancer rule as RandomRule
        return new RandomRule();
    }
}