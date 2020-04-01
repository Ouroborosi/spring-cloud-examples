# Eureka Server
The example is using Spring Cloud Hoxton SR3

## Preparation
- Gradle set up Spring Boot release trains plugin.
- Manage the Spring Cloud dependencies with Maven BOM.

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
    # register the Eureka Server itself (default is true)
    registerWithEureka: false
    # sync registered information with other Eureka Server
    fetchRegistry: false 
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
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