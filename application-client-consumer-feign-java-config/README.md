# Open Feign
The example is using Spring Cloud Hoxton SR3

Feign is integrated with **Eureka** & **Ribbon**, so the microservice is registering itself on Eureka and use the Ribbon load balancer automatically.

## Preparation
- Gradle set up Spring Boot release trains plugin.
- Manage the Spring Cloud dependencies with Maven BOM.

## Dependencies
```groovy
dependencies {
    implementation "org.springframework.cloud:spring-cloud-starter-netflix-eureka-client"
    implementation "org.springframework.cloud:spring-cloud-starter-openfeign"
}
```
## Configuration
There's no need to use _@EnableDiscoveryClient_ or _@EnableEurekaClient_ annotation since Spring Cloud Edgware.

Add _@EnableFeignClients_ annotation on App class
```java
@SpringBootApplication
@EnableFeignClients
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```

Turn on logging for Feign client
```yaml
logging:
  level:
    # !!Beware: Feign logger only response for debug level
    application.client.consumer.feign: debug
```

## Customizing Configuration - Java Code configuration
Override Feign Contract and Logger configuration
```java
/**
 * Customizing configuration class
 */
class FeignConfiguration {
    /**
     * override feign contract from default SpringMvcContract to feign.Contract.Default which supports Feign native annotations.
     * @return feignContract
     */
    @Bean
    public Contract feignContract() {
        return new feign.Contract.Default();
    }

    /**
     * override logger level from default Level.NONE to Level.FULL
     * @return Logger.Level
     */
    @Bean
    public Logger.Level logger() {
        return Logger.Level.FULL;
    }
}
```

define customizing configuration on _@FeignClient_ and use Feign native annotations instead of Spring annotations.
```java
@FeignClient(name = "microservice-provider-user", configuration = FeignConfiguration .class)
public interface UserFeignClient {
  @RequestLine("GET /users/{id}")
  User findById(@Param("id") Long id);
}
```
# Usage
1. start up eureka-server-basic
2. start up application-client-consumer-feign-java-config
3. start up application-service-provider with peer1 profile
4. start up application-service-provider with peer2 profile

The application-service-provider would create 4 user and the id is 1~4.

call API on application-client-consumer threw curl command:
```shell script
# curl curl -X GET http://{application_server_url}:{application_server_port}/order/users/{user_id}
curl -X GET http://localhost:8010/order/users/1
```