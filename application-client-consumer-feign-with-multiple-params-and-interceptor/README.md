# Open Feign for Application Client - `GET` request with Interceptor
_The example is using Spring Cloud Hoxton SR3_

Feign is integrated with **Eureka** & **Ribbon**, so the microservice is registering itself on Eureka and use the Ribbon load balancer automatically.

This example shows how to use POJO as request parameter with Feign Client in `GET` request.

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

## Feign Client Interceptor
Feign doesn't support parameter mapping as Spring so, it cannot map query string into POJO automatically. Though, Feign has a `RequestInterceptor` interface. The request template can be touched through implementation. When the interceptor detects a `GET` request it will put the contents in template `body` into template `queries`.

# Usage
1. start up eureka-server-basic
2. start up application-client-consumer-feign-with-multiple-params-and-interceptor
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
# curl "http://{application_server_url}:{application_server_port}/order/users?{parameters}"
crul "http://localhost:8010/order/users?age=20&name=Keven"
```