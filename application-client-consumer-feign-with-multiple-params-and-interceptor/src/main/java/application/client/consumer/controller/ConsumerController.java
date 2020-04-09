package application.client.consumer.controller;

import application.client.consumer.entity.User;
import application.client.consumer.feign.UserFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/order/users")
@RestController
public class ConsumerController {
  private final UserFeignClient userFeignClient;

  public ConsumerController(UserFeignClient userFeignClient) {
    this.userFeignClient = userFeignClient;
  }

  @GetMapping
  public List<User> findUsers(User user) {
    return userFeignClient.findUsers(user);
  }
}