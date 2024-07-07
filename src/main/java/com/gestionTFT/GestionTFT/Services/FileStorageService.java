package com.gestionTFT.GestionTFT.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService() {
        this.fileStorageLocation = Paths.get("C:/Users/pmas2Escritorio/Almacenamiento de ficheros TFM").toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public UrlResource loadFileAsResource(String path) {
        try {
            Path filePath = this.fileStorageLocation.resolve(path).normalize();
            UrlResource urlResource = new UrlResource(filePath.toUri());
            if(urlResource.exists()) {
                return urlResource;
            } else {
                throw new RuntimeException("File not found " + path);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + path, ex);
        }
    }
}
