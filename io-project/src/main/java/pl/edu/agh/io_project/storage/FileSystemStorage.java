package pl.edu.agh.io_project.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FileSystemStorage implements Storage {

    @Value("${storage.local.base-path}")
    private String basePath;

    @Async
    @Override
    public void store(MultipartFile file, String destinationPath) {
        if (file == null || destinationPath == null) {
            throw new IllegalArgumentException("File or destination path must not be null");
        }

        try {
            var destination = Paths.get(basePath, destinationPath);
            Files.createDirectories(destination.getParent());
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    @Override
    public Resource load(String filePath) {
        try {
            if (filePath == null || filePath.isBlank()) {
                throw new IllegalArgumentException("File path must not be null or blank");
            }

            var path = Paths.get(basePath, filePath);
            if (!Files.exists(path) || !Files.isReadable(path)) {
                throw new RuntimeException("File not found or not readable: " + filePath);
            }

            return new FileSystemResource(path);
        } catch (Exception e) {
            throw new RuntimeException("Error loading resource: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String filePath) {
        try {
            var fileToDelete = ResourceUtils.getFile(basePath + File.separator + filePath);

            if (fileToDelete.exists() && fileToDelete.isFile()) {
                if (!fileToDelete.delete()) {
                    throw new RuntimeException("Failed to delete file.");
                }
            } else {
                throw new FileNotFoundException("File not found or is not a file.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean exists(String filePath) {
        if (filePath == null || filePath.isBlank()) return false;
        var path = Paths.get(basePath, filePath);
        return Files.exists(path) && Files.isRegularFile(path);
    }

    @Override
    public void move(String sourcePath, String destinationPath) {
        try {
            var source = Paths.get(basePath, sourcePath);
            var destination = Paths.get(basePath, destinationPath);
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to move file or directory: " + e.getMessage(), e);
        }
    }

    @Override
    public void rename(String currentPath, String newName) {
        if (currentPath == null || newName == null || currentPath.isBlank() || newName.isBlank()) {
            throw new IllegalArgumentException("Current path and new name must not be null or blank");
        }

        try {
            var currentFile = Paths.get(basePath, currentPath);
            var renamedFile = currentFile.resolveSibling(newName);
            Files.move(currentFile, renamedFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to rename file: " + e.getMessage(), e);
        }
    }

    @Override
    public void copy(String sourcePath, String destinationPath) {
        if (sourcePath == null || destinationPath == null || sourcePath.isBlank() || destinationPath.isBlank()) {
            throw new IllegalArgumentException("Source and destination paths must not be null or blank");
        }

        try {
            var source = Paths.get(basePath, sourcePath);
            var destination = Paths.get(basePath, destinationPath);
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy file or directory: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateUniqueFileName(String originalFileName) {
        var dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        var timestamp = dateFormat.format(new Date());
        var uniqueId = UUID.randomUUID().toString();
        var fileExtension = getFileExtension(originalFileName);
        return timestamp + "_" + uniqueId + "." + fileExtension;
    }

    @Override
    public List<String> listFiles(String directoryPath) {
        try {
            var directory = ResourceUtils.getFile(basePath + File.separator + directoryPath);

            if (directory.exists() && directory.isDirectory()) {
                var files = directory.listFiles();
                if (files != null) {
                    return Arrays.stream(files)
                            .filter(File::isFile)
                            .map(File::getName)
                            .toList();
                }
            }
            throw new FileNotFoundException("Directory not found or is not a directory.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to list files: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteDirectory(String directoryPath) {
        try {
            var directoryToDelete = ResourceUtils.getFile(basePath + File.separator + directoryPath);

            if (directoryToDelete.exists() && directoryToDelete.isDirectory()) {
                if (!FileSystemUtils.deleteRecursively(directoryToDelete)) {
                    throw new RuntimeException("Failed to delete directory.");
                }
            } else {
                throw new FileNotFoundException("Directory not found or is not a directory.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete directory: " + e.getMessage(), e);
        }
    }

    private String getFileExtension(String originalFileName) {
        int lastDotIndex = originalFileName.lastIndexOf('.');
        return (lastDotIndex != -1) ? originalFileName.substring(lastDotIndex + 1) : "";
    }
}
