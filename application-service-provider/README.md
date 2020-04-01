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

register microservice to Eureka Server through _application.yml_
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

The application-service-provider would create 4 user and the id is 1~4.

call API on application-client-consumer threw curl command:
```shell script
# curl curl -X GET http://{application_server_url}:{application_server_port}/order/users/{user_id}
curl -X GET http://localhost:8010/order/users/1
```