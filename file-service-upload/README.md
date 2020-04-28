# Application Client 
_The example is using Spring Cloud Hoxton SR3_

Build a file upload service and see how Zuul handles files.

The small file(smaller than 1M) can be uploaded through the routes mapping URL. However, if it's a large file that we
 need to use Zuul path that provides by Zuul.
```text
/zuul/microservice-file-upload/upload
```

Otherwise, the Zuul server will throw an exception as follows:
```text
org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException: The field file exceeds its maximum permitted size of 1048576 bytes.
```

## Limitation of File Upload
The default maximum file size is 1M.
The default maximum request size is 10M

The size limitation can be set on the file upload service's `application.yml`
```yaml
spring:
  servlet:
      multipart:
        max-file-size: 2MB      # Max file size，default is 1M
        max-request-size: 25MB  # Max request size，default is 10M
```

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

Register microservice to Eureka Server through `application.yml`.
```yaml
eureka:
    client:
        serviceUrl:
        defaultZone: http://localhost:8761/eureka/
```

# Usage
1. start up eureka-server-basic
2. start up file-service-upload
3. start up zuul-server

# How to Test
## Success Case
### Upload file through Zuul Path
1. Pick one file which is smaller than 2Mb
2. call API on zuul-server through curl command:
    ```shell script
    # curl -F "file=@{file_path}" http://{zuul_server_url}:{zuul_server_port}/zuul/upload
    curl -F "file=@/User/Guest/Pictures/image.jpg" locralhost:8040/zuul/microservice-file-upload/upload
    ```
3. File upload successfully and get the location path

### Upload Super Large File through Zuul Path
1. Change the `max-file-size` & `max-request-size` larger than 2Gb on file upload server
    ```yaml
    spring:
      servlet:
          multipart:
            max-file-size: 1GB     # Max file size，default is 1M
            max-request-size: 2GB  # Max request size，default is 10M
    ```
2. Customizing Hystrix & Ribbon timeout settings on Zuul server
    ```yaml
    hystrix.command.default.isolation.thread.timeoutInMilliseconds: 60000
    
    ribbon:
      ConnectTimeout: 3000
      ReadTimeout: 6000
    ``` 
3. Pick one file which is larger than 500Mb
4. Call API on zuul-server through curl command:
   ```shell script
   # curl -F "file=@{file_path}" http://{zuul_server_url}:{zuul_server_port}/upload
   curl -F "file=@/User/Guest/Pictures/image.jpg" locralhost:8040/zuul/microservice-file-upload/upload
   ```
5. File upload successfully and get the location path

## Fail Case
### Without Using the Zuul Path
1. Pick one file which is larger than 2Mb
2. Call API on zuul-server through curl command:
   ```shell script
   # curl -F "file=@{file_path}" http://{zuul_server_url}:{zuul_server_port}/upload
   curl -F "file=@/User/Guest/Pictures/image.jpg" locralhost:8040/microservice-file-upload/upload
   ```
3. Get exception on zuul server
    ```text
    org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException: The field file exceeds its maximum permitted size of 1048576 bytes.
    ```
### Use Zuul Path for Super Large File
1. Change the `max-file-size` & `max-request-size` larger than 2Gb on file upload server
    ```yaml
    spring:
      servlet:
          multipart:
            max-file-size: 1GB     # Max file size，default is 1M
            max-request-size: 2GB  # Max request size，default is 10M
    ```
2. Pick one file which is larger than 1Gb
3. Call API on zuul-server through curl command:
   ```shell script
   # curl -F "file=@{file_path}" http://{zuul_server_url}:{zuul_server_port}/upload
   curl -F "file=@/User/Guest/Pictures/image.jpg" locralhost:8040/zuul/microservice-file-upload/upload
   ```
4. After a while, Zuul server console will throw a HystrixRuntimeException
    ```text
    com.netflix.zuul.exception.ZuulException: Forwarding error
    ... omit
    
    Caused by: com.netflix.hystrix.exception.HystrixRuntimeException: microservice-file-upload timed-out and no fallback available.
    ... omit
    ```