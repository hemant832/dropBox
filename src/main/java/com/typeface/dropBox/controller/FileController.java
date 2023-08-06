package com.typeface.dropBox.controller;

import com.typeface.dropBox.payload.UploadFileResponse;
import com.typeface.dropBox.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/uploadFile/{userId}/")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String userId) {
        String fileName = fileStorageService.storeFile(file, userId);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @GetMapping("/downloadFile/{userId}/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String userId, @PathVariable String fileName) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(userId, fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/file"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/listFiles/{userId}")
    public ResponseEntity<List<String>> getAllFiles(@PathVariable String userId) {
        List<String> fileList = fileStorageService.getAllFiles(userId);
        return ResponseEntity.ok().body(fileList);
    }
}
