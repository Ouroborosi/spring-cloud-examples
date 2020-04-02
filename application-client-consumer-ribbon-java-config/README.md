# Eureka Server
The example is using Spring Cloud Hoxton SR3

## Preparation
- Gradle set up Spring Boot release trains plugin.
- Manage the Spring Cloud dependencies with Maven BOM.

## Dependenices
```groovy
dependencies {
    implementation "org.springframework.cloud:spring-cloud-starter-netflix-eureka-client"
}
```
## Configuration
There's no need to use _@EnableDiscoveryClient_ or _@EnableEurekaClient_ annotation since Spring Cloud Edgware.

Add _@LoadBalanced_ annotation on RestTemplate bean to enable Ribbon load balancer.
```java
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
```

Call API through virtual-host name.
```java
    @GetMapping("/users/{id}")
    public User findById(@PathVariable Long id) {
        final User user = this.restTemplate.getForObject(
            "http://application-service-provider/users/{id}",
            User.class, 
            id);
        return user;
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

The configuration file in context would define the microservice that is going to use this Ribbon Client and the customizing configuration class.
```java
@Configuration
@RibbonClient(name = "application-service-provider", configuration = RibbonConfiguration.class)
public class AppRibbonConfig {
}
```

### Beware!!!
Do not add @Configuration on customizing configuration class. It shouldn't be scanned through the component scan. Otherwise, the configuration would apply to all microservices' client instead the specific client.
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
This customizing configuration is lazily loaded on the first request to the named client. It can be changed to instead eagerly load these child application contexts at startup.
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

The application-service-provider would create 4 user and the id is 1~4.

call API on application-client-consumer threw curl command:
```shell script
# curl curl -X GET http://{application_server_url}:{application_server_port}/order/users/{user_id}
curl -X GET http://localhost:8010/order/users/1
```