# Service Provider with API Authentication
_The example is using Spring Cloud Hoxton SR3_

Create secured API endpoint on the microservice.

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
> There's no need to use `@EnableDiscoveryClient` or `@EnableEurekaClient` annotation since Spring Cloud Edgware.

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

## Security Configuration
Override `HttpSecurity` configuration in `WebSecurityConfigurerAdapter`.
Use `BCryptPasswordEncoder` as password encoder.
```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * Override HttpSecurity configuration to make all request authenticate by http basic authentication
     * @param http HttpSecurity
     */
    @Override
    protected void configure(HttpSecurity http)  {
        http.authorizeRequests().anyRequest().authenticated().and().httpBasic();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

## User Authentication
Implement `UserDetailsService` and override `loadUserByUsername(...)` method.
```java
@Component
public class CustomUserDetailsService implements UserDetailsService {
    
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Mock two accounts:
     * 1. Username: user, Password: password1, Role: user-role
     * 2. Username: admin, Password: password2, Role: admin-role
     *
     * Encode the password by password encoder to match the encoded data.
     *
     * @param username use user name to load user details
     * @return UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("user".equals(username)) {
            return new SecurityUser("user", passwordEncoder.encode("password1"), "user-role");
        } else if ("admin".equals(username)) {
            return new SecurityUser("admin", passwordEncoder.encode("password2"), "admin-role");
        } else {
            return null;
        }
    }
}
``` 
Create an `SecurityUser` class which implements `UserDetails` interface, and override `getAuthorities()` method.
```java
@Data
@NoArgsConstructor
class SecurityUser implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String password;
    private String role;

    /*
     * Lombok's @AllArgsConstructor will not call super(). That's why the constructor has to be created manually.
     */
    public SecurityUser(String username, String password, String role) {
        super();
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(this.role);
        authorities.add(authority);
        return authorities;
    }
    
    // omit other override methods

}
```

# Usage
1. start up eureka-server-basic
2. start up application-client-consumer-feign-manual
3. start up application-service-provider-with-api-auth

## Data
The application-service-provider would create 3 users.

| id | username | name | age | balance |
|---|---|---|---|---|
| 1 | account1 | Keven | 20 | 100.00 |
| 2 | account2 | Logan | 28 | 180.00 |
| 3 | account3 | John | 32 | 280.00 |

## Authenticated Users
The application-service-provider would create 2 auth users as follows:

| username | password |
|---|---|
| user | password1 |
| admin | password2 |

[Login](http://localhost:8761) Eureka Server.

# How to Test
call API to check if the endpoint is available on application service:
```shell script
# curl -u username:password -X GET http://{application_server_url}:{application_server_port}/order/users/{user_id}
curl -u user:password1 -X GET http://localhost:8000/users/1
```

call API on application-client-consumer threw curl command:
```shell script
# curl -X GET http://{application_server_url}:{application_server_port}/order/users/{user_id}
curl -X GET http://localhost:8010/order/users/1
```