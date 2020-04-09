package application.client.consumer.feign;

import application.client.consumer.config.ServiceProviderConfig;
import application.client.consumer.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Do not use @RequestMapping("/users") here if the client has multiple methods with same remote API endpoint,
 * or it will cause {@link org.springframework.beans.factory.BeanCreationException}
 *
 * because cannot map '{@link application.client.consumer.feign.UserFeignClient}' method
 * {@link application.client.consumer.feign.UserFeignClient#findByAgeAndName2(Map)} to {GET /users}:
 * There is already '{@link application.client.consumer.feign.UserFeignClient}' bean method
 * {@link application.client.consumer.feign.UserFeignClient#findByAgeAndName1(Integer, String)} mapped.
 */
@FeignClient(name = ServiceProviderConfig.PROVIDER_INSTANCE_ID)
public interface UserFeignClient {

    /**
     * Will use request parameter to invoke remote /users api
     *
     * @param age user's age
     * @param name user's name
     * @return list of user data
     */
    @GetMapping("/users")
    List<User> findByAgeAndName1(@RequestParam Integer age, @RequestParam String name);

    /**
     * Will use request parameter to pass a Map which contains user's age and name
     *
     * @param map user data with age and name
     * @return list of user data
     */
    @GetMapping("/users")
    List<User> findByAgeAndName2(@RequestParam Map<String, Object> map);

    /**
     * Will pass the User into request body
     * The endpoint on Application Service has to add @RequestBody to map the POJO
     *
     * @param user user data with age and name
     * @return list of user data
     */
    @GetMapping("/users/request-body")
    List<User> findByAgeAndName3(User user);

    @PostMapping("/users")
    User addUser(User user);
}
