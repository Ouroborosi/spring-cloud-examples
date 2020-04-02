package application.client.consumer.controller;

import application.client.consumer.config.ServiceProviderConfig;
import application.client.consumer.entity.User;
import application.client.consumer.feign.UserFeignClient;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RequestMapping("/order")
@RestController
public class ConsumerController {
  private final UserFeignClient userFeignClient;

  public ConsumerController(UserFeignClient userFeignClient) {
    this.userFeignClient = userFeignClient;
  }

  @GetMapping("/users/{id}")
  public User findById(@PathVariable Long id) {
    return userFeignClient.findById(id);
  }
}