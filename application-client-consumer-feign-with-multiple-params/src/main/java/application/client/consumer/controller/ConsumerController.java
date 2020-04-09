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

  /**
   * This endpoint wil get query string mapping to User POJO
   * and set each required field as a request parameter to invoke the Feign Client
   *
   * @param user user data with age and name
   * @return list of user data
   */
  @GetMapping("/by-parameters")
  public List<User> findUser1(User user) {
    return userFeignClient.findByAgeAndName1(user.getAge(), user.getName());
  }

  /**
   * This endpoint wil get query string mapping to User POJO
   * and set the required fields into a Map to invoke the Feign Client
   *
   * @param user user data with age and name
   * @return list of user data
   */
  @GetMapping("/by-map")
  public List<User> findUser2(User user) {
    final Map<String, Object> map = new HashMap<>();
    map.put("age", user.getAge());
    map.put("name", user.getName());

    return userFeignClient.findByAgeAndName2(map);
  }

  /**
   * This endpoint wil get query string mapping to User POJO
   * pass the POJO into request body to invoke Feign Client
   *
   * @param user user data with age and name
   * @return list of user data
   */
  @GetMapping("/by-request-body")
  public List<User> findUser3(User user) {
    return userFeignClient.findByAgeAndName3(user);
  }

  @PostMapping
  public User addUser(User user) {
    return userFeignClient.addUser(user);
  }
}