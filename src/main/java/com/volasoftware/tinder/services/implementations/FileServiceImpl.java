package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.services.contracts.FileService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class FileServiceImpl implements FileService {

  @Override
  public byte[] readHtml(String filePath) {
    Resource resource = new ClassPathResource(filePath);

    byte[] contentBytes;
    try {
      contentBytes = Files.readAllBytes(Paths.get(resource.getURI()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return contentBytes;
  }
}
