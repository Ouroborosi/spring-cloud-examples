package application.client.consumer.feign;

import application.client.consumer.config.ServiceProviderConfig;
import application.client.consumer.entity.User;
import feign.Contract;
import feign.Logger;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;

@FeignClient(name = ServiceProviderConfig.PROVIDER_INSTANCE_ID)
public interface UserFeignClient {
    @RequestLine("GET /users/{id}")
    User findById(@Param("id") Long id);
}