package application.service.provider.controller;

import application.service.provider.entity.User;
import application.service.provider.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/users")
@RestController
public class ProviderController {
  private final UserRepository userRepository;

  public ProviderController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping
  public List<User> findUsers(User user) {
    return this.userRepository.findAll(Example.of(user));
  }

  @GetMapping("/request-body")
  public List<User> findUsersWithRequestBody(@RequestBody User user) {
    return this.userRepository.findAll(Example.of(user));
  }

  @PostMapping
  public User addUser(@RequestBody User user) {
    return this.userRepository.save(user);
  }
}
