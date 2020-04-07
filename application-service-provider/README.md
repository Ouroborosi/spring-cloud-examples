# Application Service
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
      # Eureka Server register endpoint
      defaultZone: http://localhost:8761/eureka/
  instance:
    # register IP address on Eureak Server (default is false and would register host name instead)
    prefer-ip-address: true
```

# Usage
1. start up eureka-server-basic
2. start up application-client-consumer
3. start up application-service-provider

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