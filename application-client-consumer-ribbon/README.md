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

Add _@LoadBalanced_ annotation on RestTemplate bean to enable Ribbon load balancer.
```java
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
```

Call API through virtual-host name.
```java
    @GetMapping("/users/{id}")
    public User findById(@PathVariable Long id) {
        final User user = this.restTemplate.getForObject(
            "http://application-service-provider/users/{id}",
            User.class, 
            id);
        return user;
    }
```

# Usage
1. start up eureka-server-basic
2. start up application-client-consumer-ribbon
3. start up application-service-provider with peer1 profile
4. start up application-service-provider with peer2 profile

The application-service-provider would create 4 user and the id is 1~4.

call API on application-client-consumer threw curl command:
```shell script
# curl curl -X GET http://{application_server_url}:{application_server_port}/order/users/{user_id}
curl -X GET http://localhost:8010/order/users/1
```