# Eureka Server
The example is using Spring Cloud Hoxton SR3

## Preparation
- Gradle set up Spring Boot release trains plugin.
- Manage the Spring Cloud dependencies with Maven BOM.

It needs to add service name in /hosts file to support localhost HA.
```text
# vim /etc/hosts
127.0.0.1 peer1 peer2
```

## Dependenices
```groovy
dependencies {
    implementation "org.springframework.boot:spring-boot-starter-web"
    implementation "org.springframework.cloud:spring-cloud-starter-netflix-eureka-server"
}
```
## Configuration
Use _@EnableEurekaServer_ annotation on main class.
```java
@SpringBootApplication
@EnableEurekaServer
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```

setup the server configuration through _application.yml_
```yaml
eureka:
  client:
    serviceUrl:
      defaultZone: http://peer2:8762/eureka/,http://peer1:8761/eureka/

# define different server peer in separated profile
---
spring:
  profiles: peer1
server:
  port: 8761
eureka:
  instance:
    hostname: peer1
---
spring:
  profiles: peer2
server:
  port: 8762
eureka:
  instance:
    hostname: peer2
```
Though Eureka Server would sync instance information between servers, but in case any unexpected issue it'd be better register all peer manually.

# Usage
1. start up eureka-server-ha with peer1 profile (use VM options -Dspring.profiles.active=peer1)
2. start up eureka-server-ha with peer2 profile (use VM options -Dspring.profiles.active=peer2)
3. start up application-client-consumer 
4. start up application-service-provider

Go to [peer1](http://peer1:8761) and [peer2](http://peer2:8762) to check if all microservices are registerd on both services.

The application-service-provider would create 4 user and the id is 1~4.

call API on application-client-consumer threw curl command:
```shell script
# curl curl -X GET http://{application_server_url}:{application_server_port}/order/users/{user_id}
curl -X GET http://localhost:8010/order/users/1
```