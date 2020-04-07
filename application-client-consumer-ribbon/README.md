# Ribbon on Application Client - Client-Side Load balancing
_The example is using Spring Cloud Hoxton SR3_

Use Ribbon - client-side load balancing to forward a request to application services.
![image](../images/Eureka%20with%20Ribbon%20architecture.png)

## Preparation
- Gradle set up Spring Boot release trains plugin.
- Manage the Spring Cloud dependencies with Maven BOM.

## Dependencies
```groovy
dependencies {
    implementation "org.springframework.cloud:spring-cloud-starter-netflix-eureka-client"
}
```
## Configuration
There's no need to use `@EnableDiscoveryClient` or `@EnableEurekaClient` annotation since Spring Cloud Edgware.

Add `@LoadBalanced` annotation on RestTemplate bean to enable Ribbon load balancer.
```java
@SpringBootApplication
public class App {
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // omit...
}
```

Call API through **_virtual-host_** name.
```java
@RequestMapping("/order")
@RestController
public class ConsumerController {
    
    // omit...

    @GetMapping("/users/{id}")
    public User findById(@PathVariable Long id) {
        return this.restTemplate.getForObject(
            "http://application-service-provider/users/{id}",
            User.class, 
            id);
    }

    // omit...
}
```

# Usage
1. start up eureka-server-basic
2. start up application-client-consumer-ribbon
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