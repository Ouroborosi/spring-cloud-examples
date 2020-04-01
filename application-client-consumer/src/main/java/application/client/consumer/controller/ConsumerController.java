package application.client.consumer.controller;

import application.client.consumer.entity.User;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RequestMapping("/order")
@RestController
public class ConsumerController {
  private final static String PROVIDER_INSTANCE_ID = "application-service-provider";

  private final RestTemplate restTemplate;

  private final DiscoveryClient discoveryClient;

  private final EurekaClient eurekaClient;

  public ConsumerController(DiscoveryClient discoveryClient, RestTemplate restTemplate, EurekaClient eurekaClient) {
    this.discoveryClient = discoveryClient;
    this.restTemplate = restTemplate;
    this.eurekaClient = eurekaClient;
  }

  @GetMapping("/users/{id}")
  public User findById(@PathVariable Long id) {
    final Application application = eurekaClient.getApplication(PROVIDER_INSTANCE_ID);
    final InstanceInfo instanceInfo = application.getInstances().get(0);
    final String url = "http://" +
            instanceInfo.getIPAddr() +
            ":" +
            instanceInfo.getPort() +
            "/users/{id}";
    final User user = this.restTemplate.getForObject(url, User.class, id);
    return user;
  }

  /**
   * get application-service-provider instance info
   * @return eureka instance info
   */
  @GetMapping("/user-instance")
  public List<ServiceInstance> showInfo() {
    return this.discoveryClient.getInstances(PROVIDER_INSTANCE_ID);
  }
}
