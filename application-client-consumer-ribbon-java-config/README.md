# Customizing the Ribbon Client - Java Configuration
_The example is using Spring Cloud Hoxton SR3_

This example will customize the Ribbon Client through the Java configuration.

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

Call API through virtual-host name.
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

## Customizing the Ribbon Client - Java code configuration
Use @Configuration & @RibbonClient to set up a customizing Ribbon Client.

Main Components which can be customized

| Interface | Spring bean name | default implementation class |
|-----|-----|-----|
| IRule | ribbonRule | ZoneAvoidanceRule |
| IPing | ribbonPing |  NoOpPing |
| ServerList | ribbonServerList | ConfigurationBasedServerList |
| ServerListFilter | ribbonServerListFilter | ZonePreferenceServerListFilter |
| ServerListUpdater | ribbonServerListUpdater | PollingServerListUpdater |
| IClientConfig | ribbonClientConfig | DefaultClientConfigImpl |
|ILoadBalancer | ribbonLoadBalancer | ZoneAwareLoadBalancer |

The configuration file in context would define the microservice that is going to use this Ribbon Client, and the customizing configuration class.
```java
@Configuration
@RibbonClient(name = "application-service-provider", configuration = RibbonConfiguration.class)
public class AppRibbonConfig {
}
```

**Beware!!!**

Do not add `@Configuration` on customizing configuration class. It shouldn't be scanned through the component scan. Otherwise, the configuration would apply to all microservices' client instead the specific client.
```java
// This package is not under the application context scan package
package application.client.config;

// ...

@Configuration
public class RibbonConfiguration {
    @Bean
    public IRule ribbonRule() {
        // define ribbon load balancer rule as RandomRule
        return new RandomRule();
    }
}
```

### Ribbon Eager Load
This customizing configuration is lazily loaded on the first request to the named client. It can be changed to instead eagerly load these child application contexts at the startup.
```yaml
# application.yml
ribbon:
  eager-load:
    enabled: true
    clients: application-service-provider
```

# Usage
1. start up eureka-server-basic
2. start up application-client-consumer-ribbon-java-config
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