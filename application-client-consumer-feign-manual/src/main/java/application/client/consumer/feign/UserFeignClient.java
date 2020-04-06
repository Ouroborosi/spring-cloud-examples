package application.client.consumer.feign;

import application.client.consumer.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/users")
@RestController
public interface UserFeignClient {
    @GetMapping(value = "/{id}")
    User findById(@PathVariable Long id);
}
