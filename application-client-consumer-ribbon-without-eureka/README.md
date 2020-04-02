# Ribbon without Eureka Server
The example is using Spring Cloud Hoxton SR3

Build an application use Ribbon load balancer, but without service discovery.

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
Add _listOfServices_ to define the service provider's location.
```yaml
# Ribbon client name 
application-service-provider:
  ribbon:
    listOfServers: peer1:8000,peer2:8010
```

**Beware!!!** <br>
Do not add _@LoadBalanced_ annotation on RestTemplate bean if trying to use Ribbon without Eureka.
After add _@LoadBalanced_ the RestTemplate will run ```RibbonLoadBalancerClient``` to check the service id on Eureka.
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

The application-service-provider would create 4 user and the id is 1~4.

call API on application-client-consumer threw curl command:
```shell script
# curl -X GET http://{application_server_url}:{application_server_port}/order/users/{user_id}
curl -X GET http://localhost:8010/order/users/1
```