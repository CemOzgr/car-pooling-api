package com.bitirme.bitirmeapi.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/images")
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(@Qualifier("image") FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping(value="/{fileName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<FileSystemResource> downloadImage(@PathVariable String fileName) {
        FileSystemResource image = fileService.download(fileName);
        if(image == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(image, HttpStatus.OK);
    }

}
