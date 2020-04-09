package application.client.consumer.controller;

import application.client.consumer.feign.UserFeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/order")
@RestController
public class ConsumerController {
  private final UserFeignClient userFeignClient;

  public ConsumerController(UserFeignClient userFeignClient) {
    this.userFeignClient = userFeignClient;
  }

  @PostMapping("/user/photo")
  public String uploadPhoto(@RequestParam MultipartFile file) {
    return userFeignClient.uploadPhoto(file);
  }
}