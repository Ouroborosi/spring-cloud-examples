package application.service.provider.controller;

import application.service.provider.entity.User;
import application.service.provider.repository.UserRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;

@RequestMapping("/users")
@RestController
public class ProviderController {
  private final UserRepository userRepository;

  public ProviderController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/{id}")
  public Optional<User> findById(@PathVariable Long id) {
    return this.userRepository.findById(id);
  }

  @PostMapping("/photo")
  public ResponseEntity<String> uploadPhoto(@RequestParam MultipartFile file) throws IOException {
      String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
      final Path path = Paths.get(fileName);

      Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
      final Resource resource = new UrlResource(path.toUri());

      return ResponseEntity.ok(resource.getURL().toString());
  }
}
