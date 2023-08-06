package com.typeface.dropBox.service;
import com.typeface.dropBox.exception.FileStorageException;
import com.typeface.dropBox.exception.FileNotFoundException;
import com.typeface.dropBox.domain.FileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorage fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file, String userId) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            Path userDirectory = this.fileStorageLocation.resolve(userId);
            Files.createDirectories(userDirectory);

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = userDirectory.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFile(String userId, String fileName) {
        try {
            Path userDirectory = this.fileStorageLocation.resolve(userId);
            Path filePath = userDirectory.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + fileName, ex);
        }
    }

    public List<String> getAllFiles(String userId) {
        List<String> fileList = new ArrayList<>();

        try {
            Path userDirectory = this.fileStorageLocation.resolve(userId);
            if (!Files.exists(userDirectory)) {
                return fileList; // Return an empty list if the user directory does not exist
            }

            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(userDirectory)) {
                for (Path path : directoryStream) {
                    if (Files.isRegularFile(path)) {
                        fileList.add(path.getFileName().toString());
                    }
                }
            }
        } catch (IOException ex) {
            // Handle the exception here or log it
            ex.printStackTrace();
        }

        return fileList;
    }
}