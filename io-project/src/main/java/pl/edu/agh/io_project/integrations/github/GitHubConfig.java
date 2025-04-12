package pl.edu.agh.io_project.integrations.github;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "github")
public class GitHubConfig {
    private Long appId;
    private String clientId;
    private String clientSecret;
    private String privateKeyPath;
}