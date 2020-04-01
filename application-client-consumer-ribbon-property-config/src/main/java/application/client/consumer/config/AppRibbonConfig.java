package application.client.consumer.config;

import application.client.config.RibbonConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Configuration;

/**
 * This class cannot be named RibbonConfig because the bean name has already taken by Ribbon source code
 * Use @RibbonClient to define which service is going to use this ribbon client setting and configure the custom configuration class
 */
@Configuration
@RibbonClient(name = "application-service-provider", configuration = RibbonConfiguration.class)
public class AppRibbonConfig {
}
