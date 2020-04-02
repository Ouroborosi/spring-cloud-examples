package application.client.consumer.controller;

import application.client.consumer.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/order")
@RestController
public class ConsumerController {
  private final static Logger LOGGER = LoggerFactory.getLogger(ConsumerController.class);
  // the chose name for LoadBalancerClient need to match the Ribbon client name set up in application.yml
  private final static String PROVIDER_INSTANCE_ID = "application-service-provider";

  private final RestTemplate restTemplate;

  private final LoadBalancerClient loadBalancerClient;

  public ConsumerController(RestTemplate restTemplate, LoadBalancerClient loadBalancerClient) {
    this.restTemplate = restTemplate;
    this.loadBalancerClient = loadBalancerClient;
  }

  /**
   * Choose the Ribbon client name which is set up in application.yml for invoke API call
   * @param id user id
   * @return User
   */
  @GetMapping("/users/{id}")
  public User findById(@PathVariable Long id) {
    final ServiceInstance instance = loadBalancerClient.choose(PROVIDER_INSTANCE_ID);
    final String url = String.format("http://%s:%s/users/{id}", instance.getHost(), instance.getPort());

    LOGGER.info("Invoke API: {}", url);

    return this.restTemplate.getForObject(url, User.class, id);
  }
}