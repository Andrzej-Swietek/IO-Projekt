package pl.edu.agh.io_project.storage;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/v1/storage")
@AllArgsConstructor
public class StorageController {

    @Autowired
    @Qualifier("localfile")
    private final Storage storage;

    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles(@RequestParam String directoryPath) {
        List<String> files = storage.listFiles(directoryPath);
        return ResponseEntity.ok(files);
    }


    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filePath) {
        Resource resource = storage.load(filePath);

        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@ModelAttribute FileUploadRequest request) {
        if (request.file().isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        storage.store(request.file(), request.destinationPath());
        return ResponseEntity.status(HttpStatus.CREATED).body("File uploaded successfully");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam String filePath) {
        storage.delete(filePath);
        return ResponseEntity.ok("File deleted successfully");
    }
}

