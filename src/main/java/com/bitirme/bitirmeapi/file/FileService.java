package com.bitirme.bitirmeapi.file;

import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    void init();

    void upload(String path, MultipartFile file);

    void delete(String path);

    FileSystemResource download(String path);
}
