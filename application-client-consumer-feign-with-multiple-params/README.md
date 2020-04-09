# Open Feign for Application Client - Multiple Parameters
_The example is using Spring Cloud Hoxton SR3_

Feign is integrated with **Eureka** & **Ribbon**, so the microservice is registering itself on Eureka and use the Ribbon load balancer automatically.

This example shows how to handle multiple parameters in Feign Client.

## Preparation
- Gradle set up Spring Boot release trains plugin.
- Manage the Spring Cloud dependencies with Maven BOM.

## Dependencies
```groovy
dependencies {
    implementation "org.springframework.cloud:spring-cloud-starter-netflix-eureka-client"
    implementation "org.springframework.cloud:spring-cloud-starter-openfeign"
    implementation "io.github.openfeign:feign-httpclient:10.7.4"
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

## Feign Client
### GET Request
For handling multiple parameters that we can easily use a POJO to map the query string/content data in Spring. However, Feign doesn't support parameter mapping like Spring. For example, Feign Client cannot map query string into POJO automatically.

Still, there are several ways to handle the multiple parameters in Feign Client as follows:
- Use `@RequestParam` instead POJO
- Use `Map` as the request parameter
- Override default HttpClient and use request body to pass data (in this case it's using apache http client)
- Implement `RequestInterceptor` to handle every request before invoke remote API 

### POST Request
Add `@RequestBody` annotation for supporting the multiple parameters.

---
Create an interface for invoke remote API
```java
/**
 * Do not use @RequestMapping("/users") here or it will cause org.springframework.beans.factory.BeanCreationException
 */
@FeignClient(name = ServiceProviderConfig.PROVIDER_INSTANCE_ID)
public interface UserFeignClient { 
    /**
      * Will fail. Feign Client doesn't support parameter mapping.
      */
    @GetMapping(value = "/users")
    List<User> findByAgeAndName0(User user);
    
    /**
      * Use `@RequestParam` instead POJO
      */
    @GetMapping(value = "/users")
    List<User> findByAgeAndName1(@RequestParam Integer age, @RequestParam String name);
    
    /**
      * Use `Map` as the request parameter
      */
    @GetMapping(value = "/users")
    List<User> findByAgeAndName2(@RequestParam Map<String, Object> map);
    
    /**
      * Use request body pass data
      */
    @GetMapping(value = "/users/request-body")
    List<User> findByAgeAndName3(User user);
}
```

# Usage
1. start up eureka-server-basic
2. start up application-client-consumer-feign-with-multiple-params
3. start up application-service-provider-with-multiple-params

## Data
The application-service-provider would create 3 users.

| id | username | name | age | balance |
|---|---|---|---|---|
| 1 | account1 | Keven | 20 | 100.00 |
| 2 | account2 | Logan | 28 | 180.00 |
| 3 | account3 | John | 32 | 280.00 |

# How to Test
call API on application client threw curl command:
```shell script
# curl "http://{application_server_url}:{application_server_port}/order/users/{endpoint}?age={age}&name={name}"
curl "http://localhost:8010/order/users/by-parameters?age=20&name=Keven"

curl "http://localhost:8010/order/users/by_map?age=20&name=Keven"

crul "http://localhost:8010/order/users/by-request-body?age=20&name=Keven"

curl -X POST --data "id=5&username=account5&name=Coco&age=22&balance=200.00" http://localhost:8010/order/users
```