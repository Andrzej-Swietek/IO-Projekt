package pl.edu.agh.io_project.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface Storage {
    void store(MultipartFile file, String destinationPath);
    Resource load(String filePath);
    void delete(String filePath);

    boolean exists(String filePath);
    void move(String sourcePath, String destinationPath);
    void rename(String currentPath, String newName);
    void copy(String sourcePath, String destinationPath);

    String generateUniqueFileName(String originalFileName);
    List<String> listFiles(String directoryPath);
    void deleteDirectory(String directoryPath);
}
