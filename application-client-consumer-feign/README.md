# Open Feign
The example is using Spring Cloud Hoxton SR3

Feign is integrated with **Eureka** & **Ribbon**, so the microservice is registering itself on Eureka and use the Ribbon load balancer automatically.

## Preparation
- Gradle set up Spring Boot release trains plugin.
- Manage the Spring Cloud dependencies with Maven BOM.

## Dependenices
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

Create an interface for invoke remote API
```java
/**
 * FeignClient name is the client service name which registered on Eureka
 * The API delegating method is using the same way as controller
 */
@FeignClient(name = ServiceProviderConfig.PROVIDER_INSTANCE_ID)
@RequestMapping("/users")
@RestController
public interface UserFeignClient {
    @GetMapping(value = "/{id}")
    User findById(@PathVariable Long id);
}
```

# Usage
1. start up eureka-server-basic
2. start up application-client-consumer-feign
3. start up application-service-provider with peer1 profile
4. start up application-service-provider with peer2 profile

The application-service-provider would create 4 user and the id is 1~4.

call API on application-client-consumer threw curl command:
```shell script
# curl curl -X GET http://{application_server_url}:{application_server_port}/order/users/{user_id}
curl -X GET http://localhost:8010/order/users/1
```