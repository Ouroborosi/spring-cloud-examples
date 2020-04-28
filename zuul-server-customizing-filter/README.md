# Open Feign for Application Client
_The example is using Spring Cloud Hoxton SR3_

Zuul integrates **Eureka**, **Ribbon** and **Hystrix**, so the microservice is registering itself on Eureka and
 use the Ribbon load balancer automatically.

In this example is gonna show you how to build your customizing Zuul filter.

## Preparation
- Gradle set up Spring Boot release trains plugin.
- Manage the Spring Cloud dependencies with Maven BOM.

## Dependencies
```groovy
dependencies {
    implementation "org.springframework.cloud:spring-cloud-starter-netflix-eureka-client"
    implementation "org.springframework.cloud:spring-cloud-starter-netflix-zuul"
}
``` 

## Configuration
There's no need to use `@EnableDiscoveryClient` or `@EnableEurekaClient` annotation since Spring Cloud Edgware.

Add `@EnableZuulProxy` annotation on App class
```java
@SpringBootApplication
@EnableZuulProxy
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```

Register zuul on Eureka
```yaml
spring:
  application:
    name: microservice-gateway-zuul

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```
## Customizing Filter
## Lifecycle
![image](../images/Zuul filter lifecycle.png)
There are several standard Filter types that correspond to the typical lifecycle of a request:

- **PRE** Filters execute before routing to the origin. Examples include request authentication, choosing origin servers
, and logging debug info.
- **ROUTE** Filters handle routing the request to an origin. This is where the origin HTTP request is built and sent
 using Apache HttpClient or Netflix Ribbon.
- **POST** Filters execute after the request has been routed to the origin. Examples include adding standard HTTP
 headers
 to the response, gathering statistics and metrics, and streaming the response from the origin to the client.
- **ERROR** Filters execute when an error occurs during one of the other phases.

Reference: [Filter Types](https://github.com/Netflix/zuul/wiki/How-it-Works#filter-types)

## Customizing
Customizing filter needs to extend the `ZuulFilter` abstract class and override `filterType()`, `filterOrder
()`, `shouldFilter()` and `run()`.

- **`filterType`:** define filter type which can be `PRE`, `ROUTE`, `POST` or `ERROR`.
- **`filterOrder`:** define filter order. The highest order advice will run first. Different type of filter can have
 the same order number.
- **`shouldFilter`:** return a `Boolean` to define the filter is active or not.
- **`run`:** filter behavior is written in this method.

# Usage
1. start up eureka-server-basic
2. start up application-client-consumer-ribbon
3. start up application-service-provider
4. start up zuul-server

## Data
The application-service-provider would create 3 users.

| id | username | name | age | balance |
|---|---|---|---|---|
| 1 | account1 | Keven | 20 | 100.00 |
| 2 | account2 | Logan | 28 | 180.00 |
| 3 | account3 | John | 32 | 280.00 |

# How to Test
