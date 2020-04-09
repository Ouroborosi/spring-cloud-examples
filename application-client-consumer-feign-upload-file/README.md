# Open Feign Upload File
_The example is using Spring Cloud Hoxton SR3_

Feign is integrated with **Eureka** & **Ribbon**, so the microservice is registering itself on Eureka and use the Ribbon load balancer automatically.

This example shows how to upload file with Feign Client.
 
## Preparation
- Gradle set up Spring Boot release trains plugin.
- Manage the Spring Cloud dependencies with Maven BOM.

## Dependencies
```groovy
dependencies {
    implementation "org.springframework.cloud:spring-cloud-starter-netflix-eureka-client"
    implementation "org.springframework.cloud:spring-cloud-starter-openfeign"
    implementation "io.github.openfeign.form:feign-form:3.8.0"
    implementation "io.github.openfeign.form:feign-form-spring:3.8.0"
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

## Feign Form
Feign Form supports for encoding application/x-www-form-urlencoded and multipart/form-data forms. After add the dependencies the only thing left is to define the Encoder configuration. In this case, we are using the Spring's standard encoder.

---
Create an interface for invoke remote API
```java
@FeignClient(name = ServiceProviderConfig.PROVIDER_INSTANCE_ID, configuration = UserFeignClient.MultipartSupportConfig.class)
@RequestMapping("/users")
public interface UserFeignClient {
    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadPhoto(MultipartFile file);

    class MultipartSupportConfig {
        @Bean
        public Encoder feignFormEncoder() {
            return new SpringFormEncoder();
        }
    }
}
```

# Usage
1. start up eureka-server-basic
2. start up application-client-consumer-feign-upload-file
3. start up application-service-provider

# How to Test
call API on application client threw curl command:
```shell script
# curl -F file=@{image_path} http://{application_server_url}:{application_server_port}/order/user/photo
curl -F file=@\Users\user1\Pictures\0.png http://localhost:8010/order/user/photo
```