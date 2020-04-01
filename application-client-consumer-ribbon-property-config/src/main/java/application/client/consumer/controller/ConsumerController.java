package application.client.consumer.controller;

import application.client.consumer.entity.User;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  private final static Logger LOGGER = LoggerFactory.getLogger(ConsumerController.class);
  private final static String PROVIDER_INSTANCE_ID = "application-service-provider";

  private final RestTemplate restTemplate;

  private final LoadBalancerClient loadBalancerClient;

  public ConsumerController(RestTemplate restTemplate, LoadBalancerClient loadBalancerClient) {
    this.restTemplate = restTemplate;
    this.loadBalancerClient = loadBalancerClient;
  }

  @GetMapping("/users/{id}")
  public User findById(@PathVariable Long id) {
    final String url = "http://" + PROVIDER_INSTANCE_ID + "/users/{id}";
    return this.restTemplate.getForObject(url, User.class, id);
  }

  /**
   * log application-service-provider instance info
   */
  @GetMapping("/log-instance")
  public void  logProviderInstance() {
    final ServiceInstance instance = this.loadBalancerClient.choose(PROVIDER_INSTANCE_ID);

    // print provider instance host and port
    ConsumerController.LOGGER.info("{}:{}:{}", instance.getServiceId(), instance.getHost(), instance.getPort());
  }
}