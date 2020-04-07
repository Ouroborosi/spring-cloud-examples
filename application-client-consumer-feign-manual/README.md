# Customizing Open Feign Client for Application Client with 
_The example is using Spring Cloud Hoxton SR3_

Feign is integrated with **Eureka** & **Ribbon**, so the microservice is registering itself on Eureka and use the Ribbon load balancer automatically.

This example which creates two Feign Clients with the same interface but configures each one with a separate request interceptor.

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

Add `@Import(FeignClientsConfiguration.class)` above the controller class.
```java
// FeignClientsConfiguration.class is the default configuration provided by Spring Cloud Netflix.
@Import(FeignClientsConfiguration.class)
@RequestMapping("/order")
@RestController
public class ConsumerController {
    // omit...
}
```

**Beware!!**

Because the example is using customizing Feign Clients there's no need to add `@FeignClient` on Feign Clients and `@EnableFeignClients` on Spring Boot main class.

## Customizing Feign Client
To create Feign clients manually the project is using Feign Builder API.

In this example would create two Feign Clients with the same interface, but configure each one with a separate `BasicAuthRequestInterceptor`. Please refer the [README](../application-service-provider-with-api-auth/README.md#Authenticated-Users) in application-service-provider-with-api-auth for the HTTP basic auth info.
```java
@Import(FeignClientsConfiguration.class)
@RequestMapping("/order")
@RestController
public class ConsumerController {
    public ConsumerController(Decoder decoder, Encoder encoder, Client client, Contract contract) {
        final Feign.Builder builder = Feign.builder()
                .client(client)
                .encoder(encoder)
                .decoder(decoder)
                .contract(contract);
    
        this.userFeignClient = builder
                .requestInterceptor(new BasicAuthRequestInterceptor("user", "password1"))
                .target(UserFeignClient.class, "http://application-service-provider/");
    
        this.adminFeignClient = builder
                .requestInterceptor(new BasicAuthRequestInterceptor("admin", "password2"))
                .target(UserFeignClient.class, "http://application-service-provider");
    }

    // omit...
}
```
> The Feign `Contract` object defines what annotations and values are valid on interfaces. The autowired `Contract` bean provides supports for SpringMVC annotations, instead of the default Feign native annotations.


Use separated Feign Clients to invoke the REST API on application service.
```java
@Import(FeignClientsConfiguration.class)
@RequestMapping("/order")
@RestController
public class ConsumerController {

    //omit...
    
    @GetMapping("/user-user/{id}")
    public User findUserById(@PathVariable Long id) {
        return userFeignClient.findById(id);
    }
    
    @GetMapping("/user-admin/{id}")
    public User findAdminById(@PathVariable Long id) {
        return adminFeignClient.findById(id);
    }
}
```

Create an interface for invoke remote API. <br>
Again, do not add `@FeignClient` in the interface. Feign Clients has already created manually on the controller.
```java
/**
 * No needs to add @FeignClient.
 * 
 */
@RequestMapping("/users")
@RestController
public interface UserFeignClient {
    @GetMapping(value = "/{id}")
    User findById(@PathVariable Long id);
}
```

# Usage
1. start up eureka-server-basic
2. start up application-client-consumer-feign-manual
3. start up application-service-provider-with-api-auth

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