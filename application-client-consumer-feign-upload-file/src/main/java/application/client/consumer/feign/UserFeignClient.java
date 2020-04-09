package application.client.consumer.feign;

import application.client.consumer.config.ServiceProviderConfig;
import application.client.consumer.entity.User;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
