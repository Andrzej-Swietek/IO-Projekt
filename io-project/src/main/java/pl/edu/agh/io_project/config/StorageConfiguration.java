package pl.edu.agh.io_project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.edu.agh.io_project.storage.FileSystemStorage;
import pl.edu.agh.io_project.storage.Storage;

@Configuration
public class StorageConfiguration {

    @Configuration
    @Profile("local")
    public static class LocalStorageConfig {
        @Bean(name = "localfile")
        public Storage storage() {
            return new FileSystemStorage();
        }
    }

}
