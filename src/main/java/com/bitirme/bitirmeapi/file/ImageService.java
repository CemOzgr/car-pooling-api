package com.bitirme.bitirmeapi.file;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service("image")
public class ImageService implements FileService{

    private final Path root = Paths.get("images");

    @Override
    public void init() {
        try{
            if(!Files.exists(root)) Files.createDirectory(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder upload");
        }
    }

    @Override
    public void upload(String fileName, MultipartFile image) {
        if(!Objects.requireNonNull(image.getContentType()).matches("image/.+")) {
            throw new IllegalStateException("Invalid format: " + image.getContentType());
        }
        try {
            Files.copy(
                    image.getInputStream(),
                    this.root.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING
            );
        }catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not save the file: " + e.getMessage());
        }
    }

    @Override
    public void delete(String fileName) {
        try {
            Files.deleteIfExists(root.resolve(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Image does not exist");
        }
    }

    @Override
    public FileSystemResource download(String fileName) {
        Path path = root.resolve(fileName);
        FileSystemResource resource = new FileSystemResource(path);

        if(!resource.exists()) return null;

        if(!resource.isReadable()) {
            throw new RuntimeException("Could not read the file");
        }
        return resource;
    }
}
