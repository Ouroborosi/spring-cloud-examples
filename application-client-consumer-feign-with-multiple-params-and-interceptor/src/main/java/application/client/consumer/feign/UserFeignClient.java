package application.client.consumer.feign;

import application.client.consumer.config.ServiceProviderConfig;
import application.client.consumer.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = ServiceProviderConfig.PROVIDER_INSTANCE_ID)
public interface UserFeignClient {

    @GetMapping("/users")
    List<User> findUsers(User user);
}
