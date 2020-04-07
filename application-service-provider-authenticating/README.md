# Application Service with authenticated Eureka Server
_The example is using Spring Cloud Hoxton SR3_

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

register microservice to Eureka Server through `application.yml`
```yaml
spring:
  application:
    # define application name on Eureka Server
    name: microservice-provider-user 
eureka:
  client:
    serviceUrl:
      # Eureka Server register endpoint with user & password
      defaultZone: http://user:password123@localhost:8761/eureka/
  instance:
    # register IP address on Eureak Server (default is false and would register host name instead)
    prefer-ip-address: true
```

# Usage
1. start up eureka-server-authenticating
2. start up application-client-consumer-authenticating
3. start up application-service-provider-authenticating

## Authentication
This example sets up the authentication for Eureka Server. The auth info as follows:

| username | password |
|---|---|
| user | password123 |

[Login](http://localhost:8761) Eureka Server.

# How to Test
call API on application-client-consumer threw curl command:
```shell script
# curl -X GET http://{username}:{password}@{application_server_url}:{application_server_port}/order/users/{user_id}
curl -X GET http://user:password1234@localhost:8000/order/users/1
```