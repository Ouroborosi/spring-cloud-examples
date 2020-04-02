package application.client.consumer.feign;

import application.client.consumer.config.ServiceProviderConfig;
import application.client.consumer.entity.User;
import feign.Contract;
import feign.Logger;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;

@FeignClient(name = ServiceProviderConfig.PROVIDER_INSTANCE_ID, configuration = FeignConfiguration.class)
public interface UserFeignClient {
    @RequestLine("GET /users/{id}")
    User findById(@Param("id") Long id);
}

/**
 * Customizing configuration class
 */
class FeignConfiguration {
    /**
     * override feign contract from default SpringMvcContract to feign.Contract.Default which supports Feign native annotations.
     * @return feignContract
     */
    @Bean
    public Contract feignContract() {
        return new feign.Contract.Default();
    }

    /**
     * override logger level from default Level.NONE to Level.FULL
     * @return Logger.Level
     */
    @Bean
    public Logger.Level logger() {
        return Logger.Level.FULL;
    }
}