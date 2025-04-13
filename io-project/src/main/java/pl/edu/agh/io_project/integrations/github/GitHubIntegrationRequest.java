package pl.edu.agh.io_project.integrations.github;

import lombok.Data;

@Data
public class GitHubIntegrationRequest {
    private Long projectId;
    private String repoOwner;
    private String repoName;
    private Long installationId;
}
