package application.file.upload.controller;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@RestController
public class FileUploadController {
  @PostMapping(value = "/upload")
  public @ResponseBody String handleFileUpload(@RequestParam(value = "file") MultipartFile file) throws IOException {
    byte[] bytes = file.getBytes();
    File fileToSave = new File(Objects.requireNonNull(file.getOriginalFilename()));
    FileCopyUtils.copy(bytes, fileToSave);
    return fileToSave.getAbsolutePath();
  }
}
