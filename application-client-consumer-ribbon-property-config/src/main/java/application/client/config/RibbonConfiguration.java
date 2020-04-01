package application.client.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 * Ribbon Configuration Class
 * Configure the customizing Ribbon Client doesn't need this class.
 * The class is only leave for validating if the ribbonRule bean has been change to the customized rule
 */
@Configuration
public class RibbonConfiguration {
    /**
     * The bean name ribbonRule is defined by Ribbon
     */
    @Autowired
    private IRule ribbonRule;

    /**
     * Check heck if the property customizing Ribbon Client is effected
     * @return
     */
    @Bean
    public Optional<Void> validate() {
        if (!(ribbonRule instanceof RandomRule)) {
            throw new RuntimeException("The Customizing Ribbon Client rule is not effected!!!");
        }
        return Optional.empty();
    }
}