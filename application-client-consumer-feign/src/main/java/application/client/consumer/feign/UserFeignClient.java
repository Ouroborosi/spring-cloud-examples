package application.client.consumer.feign;

import application.client.consumer.config.ServiceProviderConfig;
import application.client.consumer.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@FeignClient(name = ServiceProviderConfig.PROVIDER_INSTANCE_ID)
@RequestMapping("/users")
@RestController
public interface UserFeignClient {
    @GetMapping(value = "/{id}")
    User findById(@PathVariable Long id);
}
