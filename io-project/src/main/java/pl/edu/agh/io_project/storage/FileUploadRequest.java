package pl.edu.agh.io_project.storage;

import org.springframework.web.multipart.MultipartFile;

public record FileUploadRequest(
        MultipartFile file,
        String destinationPath
) {
}
