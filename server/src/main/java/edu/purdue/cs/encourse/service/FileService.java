package edu.purdue.cs.encourse.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    String storeFile(MultipartFile file);
    Resource loadFileAsResource(String fileName);
}
