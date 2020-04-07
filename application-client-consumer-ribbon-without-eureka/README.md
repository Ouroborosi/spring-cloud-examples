# Ribbon without Eureka Server
_The example is using Spring Cloud Hoxton SR3_

Build an application use Ribbon load balancer, but without service discovery.
![image](../images/Ribbon%20without%20Eureka.png)

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
Add `listOfServices` to define the service provider's location.
```yaml
# Ribbon client name 
application-service-provider:
  ribbon:
    listOfServers: peer1:8000,peer2:8010
```

**Beware!!!**

Do not add `@LoadBalanced` annotation on RestTemplate bean if trying to use Ribbon without Eureka.
After add `@LoadBalanced` the RestTemplate will run `RibbonLoadBalancerClient` to check the service id on Eureka.
```java
@SpringBootApplication
public class App {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    // omit...

}
```

Call API through virtual-host name.
```java
@RequestMapping("/order")
@RestController
public class ConsumerController {
    
    // omit...
     
    /**
       * Choose the Ribbon client name which is set up in application.yml for invoke API call
       * @param id user id
       * @return User
       */
      @GetMapping("/users/{id}")
      public User findById(@PathVariable Long id) {
        final ServiceInstance instance = loadBalancerClient.choose("application-service-provider");
        final String url = String.format("http://%s:%s/users/{id}", instance.getHost(), instance.getPort());
        return this.restTemplate.getForObject(url, User.class, id);
      }
}
```

# Usage
1. start up application-client-consumer-ribbon-without-eureka
2. start up application-service-provider with peer1 profile
3. start up application-service-provider with peer2 profile

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