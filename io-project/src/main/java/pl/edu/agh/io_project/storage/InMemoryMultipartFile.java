package pl.edu.agh.io_project.storage;

import org.springframework.mock.web.MockMultipartFile;

public class InMemoryMultipartFile extends MockMultipartFile {
    public InMemoryMultipartFile(String filename, byte[] content) {
        super(filename, filename, "application/pdf", content);
    }
}
