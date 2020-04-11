package application.client.consumer.controller;

import application.client.consumer.entity.User;
import application.client.consumer.exception.BusinessException;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequestMapping("/order")
@RestController
public class ConsumerController {
  private final static String PROVIDER_INSTANCE_ID = "application-service-provider";

  private final RestTemplate restTemplate;

  private final EurekaClient eurekaClient;

  public ConsumerController(RestTemplate restTemplate, EurekaClient eurekaClient) {
    this.restTemplate = restTemplate;
    this.eurekaClient = eurekaClient;
  }

  @HystrixCommand(fallbackMethod = "findByIdFallback")
  @GetMapping("/users/{id}")
  public User findById(@PathVariable Long id) {
    final Application application = eurekaClient.getApplication(PROVIDER_INSTANCE_ID);
    final InstanceInfo instanceInfo = application.getInstances().get(0);
    final String url = "http://" +
            instanceInfo.getIPAddr() +
            ":" +
            instanceInfo.getPort() +
            "/users/{id}";

    return this.restTemplate.getForObject(url, User.class, id);
  }

  @HystrixCommand(fallbackMethod = "findByNameFallback",
          commandProperties = {
              @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
                      value = "3000"),
              @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",
                      value = "2000"),
              @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds",
                      value = "10000")
          },
          threadPoolProperties = {
              @HystrixProperty(name = "coreSize", value = "1"),
              @HystrixProperty(name = "maxQueueSize", value = "-1")
          }
  )
  @GetMapping("/users/name/{name}")
  public List<User> findByName(@PathVariable String name) {
    final Application application = eurekaClient.getApplication(PROVIDER_INSTANCE_ID);
    final InstanceInfo instanceInfo = application.getInstances().get(0);
    final String url = "http://" +
            instanceInfo.getIPAddr() +
            ":" +
            instanceInfo.getPort() +
            "/users/name/{name}";

    return Arrays.asList(Objects.requireNonNull(this.restTemplate.getForObject(url, User[].class, name)));
  }

  /**
   * If there's no fallback method be assigned the Hystrix will throw Exception directly
   */
  @HystrixCommand
  @GetMapping("/users/{name}/age")
  public Integer findAgeByName(@PathVariable String name) {
    final Application application = eurekaClient.getApplication(PROVIDER_INSTANCE_ID);
    final InstanceInfo instanceInfo = application.getInstances().get(0);
    final String url = "http://" +
            instanceInfo.getIPAddr() +
            ":" +
            instanceInfo.getPort() +
            "/users/{name}/age";

    return this.restTemplate.getForObject(url, Integer.class, name);
  }

  /**
   * The Exception extends {@link HystrixBadRequestException} will not trigger fallback method
   */
  @HystrixCommand(fallbackMethod = "NotTriggeredBalanceFallback")
  @GetMapping("/users/{name}/balance")
  public BigDecimal findBalanceByName(@PathVariable String name) {
    throw new BusinessException("force causing BusinessException!", new Throwable("Force Throwable"));
  }

  /**
   * The {@link HystrixCommand} assigns {@link HystrixCommand#ignoreExceptions()}
   * which is going to ignore the specific system Exceptions and will not trigger fallback method
   */
  @HystrixCommand(fallbackMethod = "NotTriggeredUserNameFallback",
          ignoreExceptions = {IllegalArgumentException.class}
  )
  @GetMapping("/users/{name}/username")
  public Integer findUserNameByName(@PathVariable String name) {
    throw new IllegalArgumentException("force causing IllegalArgumentException!");
  }

  /**
   * Fallback method has to have the compatible return type of the calling method
   */
  public static User findByIdFallback(Long id) {
    log.error("Entering the fallback method");
    return new User(id, "Default User", "Default User", 0, new BigDecimal(1));
  }

  /**
   * Fallback method has to have the compatible return type of the calling method
   * This fallback will show the Exception on the console through the {@link Throwable}
   */
  public static List<User> findByNameFallback(String name, Throwable throwable) {
    log.error("Entering the fallback method", throwable);

    long randomId = ThreadLocalRandom.current().nextLong(5, 11);
    final User defaultUser = new User(randomId, "Default User", "Default User", 0, new BigDecimal(1));

    return Collections.singletonList(defaultUser);
  }

  /**
   * This fallback method should not be triggered
   * because the calling methods are using {@link HystrixBadRequestException} or {@link HystrixCommand#ignoreExceptions}
   */
  public BigDecimal NotTriggeredBalanceFallback(String name, Throwable throwable) {
    log.error("This fallback should not be triggered", throwable);

    return new BigDecimal(0);
  }

  /**
   * This fallback method should not be triggered
   * because the calling methods are using {@link HystrixBadRequestException} or {@link HystrixCommand#ignoreExceptions}
   */
  public Integer NotTriggeredUserNameFallback(String name, Throwable throwable) {
    log.error("This fallback should not be triggered", throwable);

    return 0;
  }
}
