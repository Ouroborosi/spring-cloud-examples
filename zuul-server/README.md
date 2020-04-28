# Zuul Gateway Service
_The example is using Spring Cloud Hoxton SR3_

Zuul integrates **Eureka**, **Ribbon** and **Hystrix**, so the microservice is registering itself on Eureka and
 use the Ribbon load balancer automatically.

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

## File Upload Timeout
Sometimes the upload file through Zuul is too large to be uploaded. This might threw timeout exception cause by Hystrix.
In this case we need to set up the timeout duration manually on Zuul Server.
```yaml
hystrix.command.default.isolation.thread.timeoutInMilliseconds: 60000

ribbon:
  ConnectTimeout: 3000
  ReadTimeout: 6000
```

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
## Basic Case
1. Start up eureka-server-basic, application-client-consumer, application-service-provider services and zuul-server
2. Invoke REST API on zuul server
    ```shell script
    # curl -X GET http://{zuul_server}:{zuul_server_port}/order/users/{user_id}
    curl -X GET http://localhost:8040/application-client-consumer/order/users/1
    ```
3. Get the response data successfully

## Change the proxy routes
1. Start up eureka-server, application-client-consumer and application-service-provider services 
2. Start up zuul-server with `change-proxy-routes` profile
    ```
    -Dspring.profiles.active=define-service-path
    ```
3. Invoke REST API on zuul server
   ```shell script
   curl -X GET http://localhost:8040/ordersys/order/users/1
   ```
4. Get the response data successfully

## Ignore Services
1. Start up eureka-server, application-client-consumer and application-service-provider services 
2. Start up zuul-server with `ignore-services` profile
    ```
    -Dspring.profiles.active=ignore-services
    ```
3. Invoke REST API on zuul server
    ```shell script
    curl -X GET http://localhost:8040/application-client-consumer/order/users/1
    ```
4. Get 404 Not Found message
    ```json
    {"timestamp":"2020-04-22T04:03:03.231+0000","status":404,"error":"Not Found","message":"No message available","path":"/application-client-consumer/order/users/1"}
    ```

## Explicitly Configured Routes Map
1. Start up eureka-server, application-client-consumer and application-service-provider services 
2. Start up zuul-server with `explicitly-configured-routes-map` profile
    ```
    -Dspring.profiles.active=explicitly-configured-routes-map
    ```
3. Invoke application client REST API on zuul server
   ```shell script
   curl -X GET http://localhost:8040/ordersys/order/users/1
   ```
4. Get the response data successfully
5. Invoke application service REST API on zuul server
   ```shell script
   curl -X GET http://localhost:8040/application-service-provider/users/1
   ```
6. Get 404 Not Found message
   ```json
   {"timestamp":"2020-04-22T04:20:24.284+0000","status":404,"error":"Not Found","message":"No message available","path":"/application-service-provider/users/1"}
   ```

## With ServiceId and Path
1. Start up eureka-server, application-client-consumer and application-service-provider services 
2. Start up zuul-server with `with-service-id` profile
    ```
    -Dspring.profiles.active=with-service-id
    ```
3. Invoke application client REST API on zuul server
   ```shell script
   curl -X GET http://localhost:8040/ordersys/order/users/1
   ```
4. Get the response data successfully

## With Path and URL
> These simple url-routes do not get executed as a HystrixCommand, nor do they load-balance multiple URLs with Ribbon. 

1. Start up eureka-server, application-client-consumer and application-service-provider services 
2. Start up zuul-server with `with-path-and-url` profile
    ```
    -Dspring.profiles.active=with-with-path-and-url
    ```
3. Invoke application client REST API on zuul server
   ```shell script
   curl -X GET http://localhost:8040/ordersys/order/users/1
   ```
4. Get the response data successfully

## With Path and URL. Also, not affect Zuul's Hystrix and Ribbon
1. Start up eureka-server, application-client-consumer and application-service-provider services 
2. Start up zuul-server with `with-path-and-url-also-not-affect-hystrix-and-ribbon` profile
    ```
    -Dspring.profiles.active=with-path-and-url-also-not-affect-hystrix-and-ribbon
    ```
3. Invoke application client REST API on zuul server
   ```shell script
   curl -X GET http://localhost:8040/ordersys/order/users/1
   ```
4. Get the response data successfully

## With Prefix
1. Start up eureka-server, application-client-consumer and application-service-provider services 
2. Start up zuul-server with `with-prefix` profile
    ```
    -Dspring.profiles.active=with-prefix
    ```
3. Invoke application client REST API on zuul server with prefix
   ```shell script
   curl -X GET http://localhost:8040/api/ordersys/order/users/1
   ```
4. Get the response data successfully
5. Invoke application service REST API on zuul server with prefix
    ```shell script
    curl -X GET http://localhost:8040/api/application-service-provider/users/1
    ```
6. Get the response data successfully

# Strip Prefix
1. Start up eureka-server, application-client-consumer and application-service-provider services 
2. Start up zuul-server with `strip-prefix` profile
    ```
    -Dspring.profiles.active=strip-prefix
    ```
3. Invoke application client REST API on zuul server with prefix
   ```shell script
   curl -X GET http://localhost:8040/order/users/1
   ```
4. Get the response data successfully

## Ignore Specific Routes Map
1. Start up eureka-server, application-client-consumer and application-service-provider services 
2. Start up zuul-server with `ignore-specific-routes-map` profile
    ```
    -Dspring.profiles.active=ignore-specific-routes-map
    ```
3. Invoke application client REST API on zuul server
    ```shell script
    curl -X GET http://localhost:8040/ordersys/order/users/1
    ```
4. Get the response data successfully
5. Invoke application client REST API on zuul server
    ```shell script
    curl -X GET http://localhost:8040/ordersys/order/user-instance
    ```
6. Get 404 Not Found message
   ```json
   {"timestamp":"2020-04-22T05:32:17.760+0000","status":404,"error":"Not Found","message":"No message available","path":"/ordersys/order/user-instance"}
   ```
