package application.service.provider.controller;

import application.service.provider.entity.User;
import application.service.provider.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Optional;

@RequestMapping("/users")
@RestController
public class ProviderController {
  private final static Logger LOGGER = LoggerFactory.getLogger(ProviderController.class);

  private final UserRepository userRepository;

  public ProviderController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/{id}")
  public Optional<User> findById(@PathVariable Long id) {
    final Object principal = SecurityContextHolder.getContext().getAuthentication();

    if (principal instanceof UserDetails) {
      final UserDetails user = (UserDetails) principal;
      final Collection<? extends GrantedAuthority> collection = user.getAuthorities();
      for (GrantedAuthority c : collection) {
        // Logging current user info
        ProviderController.LOGGER.info("The current username is {}, and the role is {}.", user.getUsername(), c.getAuthority());
      }
    }

    return this.userRepository.findById(id);
  }
}
