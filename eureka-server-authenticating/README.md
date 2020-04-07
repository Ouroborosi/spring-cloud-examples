# Eureka Server Authentication
_The example is using Spring Cloud Hoxton SR3_

## Preparation
- Gradle set up Spring Boot release trains plugin.
- Manage the Spring Cloud dependencies with Maven BOM.

It needs to add service name in /hosts file to support localhost HA.
```text
# vim /etc/hosts
127.0.0.1 peer1 peer2
```

## Dependencies
```groovy
dependencies {
    implementation "org.springframework.boot:spring-boot-starter-web"
    implementation "org.springframework.boot:spring-boot-starter-security"
    implementation "org.springframework.cloud:spring-cloud-starter-netflix-eureka-server"
}
```
## Configuration
Use `@EnableEurekaServer` annotation on main class.
```java
@SpringBootApplication
@EnableEurekaServer
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```
Enable Spring Security with /eureka/** url.
```java
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().ignoringAntMatchers("/eureka/**");
    super.configure(http);
  }
}
```

setup the server configuration through `application.yml`
```yaml
spring:
  application:
    # register name on eureka server
    name: eureka-server-authenticating
  security:
    user:
      name: user
      password: password123
```
Use Spring Security to do the authentication. To prevent that someone might send unauthenticated DELETE request to destroy the service instances on Eureka Server.

# Usage
1. start up eureka-server-authenticating
2. start up application-client-consumer-authenticating
3. start up application-service-provider-authenticating

## Authenticated Users
The application-service-provider would create 2 auth users as follows:

| username | password |
|---|---|
| user | password1 |
| admin | password2 |

[Login](http://localhost:8761) Eureka Server <br>

# How to Test
call API on application-client-consumer threw curl command:
```shell script
# curl -X GET http://{username}:{password}@{application_server_url}:{application_server_port}/order/users/{user_id}
curl -X GET http://user:password1234@localhost:8010/order/users/1
```