# Open Feign for Application Client
_The example is using Spring Cloud Hoxton SR3_

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
There's no need to use `@EnableDiscoveryClient` or `@EnableEurekaClient` annotation since Spring Cloud Edgware.

Add `@EnableFeignClients` annotation on App class
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

## Data
The application-service-provider would create 3 users.

| id | username | name | age | balance |
|---|---|---|---|---|
| 1 | account1 | Keven | 20 | 100.00 |
| 2 | account2 | Logan | 28 | 180.00 |
| 3 | account3 | John | 32 | 280.00 |

# How to Test
call API on application-client-consumer threw curl command:
```shell script
# curl -X GET http://{application_server_url}:{application_server_port}/order/users/{user_id}
curl -X GET http://localhost:8010/order/users/1
```

### High Availability Testing
1. Start up Eureka server and services
2. Invoke REST API on application client(i.e. _`application-client-consumer`_ in this example) and get the response data
3. Shutdown one of the application service(i.e. _`application-service-provider`_ in this example)
4. Invoke REST API on application client again and still get the response data successfully