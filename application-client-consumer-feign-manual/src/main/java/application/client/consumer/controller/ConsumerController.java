package application.client.consumer.controller;

import application.client.consumer.entity.User;
import application.client.consumer.feign.UserFeignClient;
import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// FeignClientsConfiguration.class is the default configuration provided by Spring Cloud Netflix.
@Import(FeignClientsConfiguration.class)
@RequestMapping("/order")
@RestController
public class ConsumerController {
  private UserFeignClient userFeignClient;
  private UserFeignClient adminFeignClient;

  public ConsumerController(Decoder decoder, Encoder encoder, Client client, Contract contract) {
    final Feign.Builder builder = Feign.builder()
            .client(client)
            .encoder(encoder)
            .decoder(decoder)
            .contract(contract);

    this.userFeignClient = builder
            .requestInterceptor(new BasicAuthRequestInterceptor("user", "password1"))
            .target(UserFeignClient.class, "http://application-service-provider/");

    this.adminFeignClient = builder
            .requestInterceptor(new BasicAuthRequestInterceptor("admin", "password2"))
            .target(UserFeignClient.class, "http://application-service-provider");
  }

  @GetMapping("/user-user/{id}")
  public User findUserById(@PathVariable Long id) {
    return userFeignClient.findById(id);
  }

  @GetMapping("/user-admin/{id}")
  public User findAdminById(@PathVariable Long id) {
    return adminFeignClient.findById(id);
  }
}