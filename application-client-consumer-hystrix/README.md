# Application Client with Hystrix
_The example is using Spring Cloud Hoxton SR3_

This example is using Hystrix to test the fault tolerance.

## Preparation
- Gradle set up Spring Boot release trains plugin.
- Manage the Spring Cloud dependencies with Maven BOM.

## Dependencies
```groovy
dependencies {
    implementation "org.springframework.cloud:spring-cloud-starter-netflix-eureka-client"
    implementation "org.springframework.cloud:spring-cloud-starter-netflix-hystrix"
}
```

## Configuration
There's no need to use `@EnableDiscoveryClient` or `@EnableEurekaClient` annotation since Spring Cloud Edgware.

register microservice to Eureka Server through `application.yml`
```yaml
spring:
  application:
    # register name on eureka server
    name: application-client-consumer
eureka:
  client:
    serviceUrl:
      # Eureka Server register endpoint
      defaultZone: http://localhost:8761/eureka/
  instance:
    # register IP address on Eureak Server (default is false and would register host name instead)
    prefer-ip-address: true
```
## Include Hystrix
Enable Hystrix with `@EnableHystrix` or `@EnableCircuitBreaker`.
```java
@SpringBootApplication
@EnableHystrix
public class ConsumerMovieApplication {
	// omit...
}
```

Graceful degradation can be achieved by declaring name of fallback method in `@HystrixCommand`. It will provide the fallback wherever feasible to protect users from failure.
```java
@RequestMapping("/order")
@RestController
@Slf4j
public class ConsumerController {
    // omit...
    
    @HystrixCommand(fallbackMethod = "findByIdFallback")
    @GetMapping("/users/{id}")
    public User findById(@PathVariable Long id) {
        // omit...
    }
    
    /**
      * fallback method has to have the compatible return type of the calling method
      */
    public User findByIdFallback(Long id, Throwable throwable) {
        log.error("Entering the fallback method", throwable);
        return new User(id, "Default User", "Default User", 0, new BigDecimal(1));
    }
}
```

### Hystrix Granularity
Hystrix provides `@HystrixProperty` annotation to control the granularity of `@HystrixCommand` behavior through the [command properties](https://github.com/Netflix/Hystrix/wiki/Configuration#CommandProperties).
 ```java
@RequestMapping("/order")
@RestController
@Slf4j
public class ConsumerController {
    // omit...

    @HystrixCommand(fallbackMethod = "findByNameFallback",
            commandProperties = {
                @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
                        value = "3000"),
                @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold00",
                        value = "2"),
                @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds",
                        value = "10000")
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
    // omit...
}
```

# Usage
1. start up eureka-server-basic
2. start up application-client-consumer
3. start up application-service-provider

## Data
The application service would create 3 users.

| id | username | name | age | balance |
|---|---|---|---|---|
| 1 | account1 | Keven | 20 | 100.00 |
| 2 | account2 | Logan | 28 | 180.00 |
| 3 | account3 | John | 32 | 280.00 |

# How to Test
## Endpoints
### /users/{id}
This endpoint assign a fallback method to do the graceful degradation. The method will return a predefined default user data.

**Step1:** call API on application client threw curl command:
```shell script
# curl -X GET http://{application_server_url}:{application_server_port}/order/users/{user_id}
curl -X GET http://localhost:8010/order/users/1
```
The result of request should be the user data from application service.

**Step2:** shutdown the application service.

**Step3:** call the API again.

**Step4:** receive the user data set in fallback method.
```json
{"id":1,"username":"Default User","name":"Default User","age":0,"balance":1}
```

### /users/name/{name}
This endpoint invokes a none existed remote API. The case shows how to control the granularity through the command properties.

**Step1:** call API on application client threw curl command:
```shell script
# curl -X GET http://{application_server_url}:{application_server_port}/order/users/name/{name}
curl -X GET http://localhost:8010/order/users/name/Keven
```

**Step2:** because the remote API does not exist. The fallback method will be invoked to do the graceful degradation which returns a predefined default user data.
```json
{"id":1,"username":"Default User","name":"Default User","age":0,"balance":1}
```

**Step3:** the fallback method in this case has Throwable. Check the console to see if there shows exception.

### /users/{name}/age
This endpoint invokes a none existed remote API. The case does not assign any fallback method. Base on Hystrix default fallback mechanism the exception will be threw directly.

**Step1:** call API on application client threw curl command:
```shell script
# curl -X GET http://{application_server_url}:{application_server_port}/order/users/{name}/age
curl -X GET http://localhost:8010/order/users/Keven/age
```

**Step2:** Check the console to see if there shows exception.

### /users/{name}/balance
This endpoint invokes a none existed remote API. The case throws, a customizing exception, the exception extends `HystrixBadRequestException` which should not trigger assigned fallback method.

**Step1:** call API on application client threw curl command:
```shell script
# curl -X GET http://{application_server_url}:{application_server_port}/order/users/{name}/balance
curl -X GET http://localhost:8010/order/users/Keven/balance
```

**Step2:** Check the console to see if there shows exception and **not** showing the error log `This fallback should not be triggered`.

### /users/{name}/username
This endpoint invokes a none existed remote API. The case defined `ignoreException` in `@HystrixCommand`. So, there should not show the error log `This fallback should not be triggered` on the console.

**Step1:** call API on application client threw curl command:
```shell script
# curl -X GET http://{application_server_url}:{application_server_port}/order/users/{name}/username
curl -X GET http://localhost:8010/order/users/Keven/username
```

**Step2:** Check the console to see if there shows exception and **not** showing the error log `This fallback should not be triggered`.
