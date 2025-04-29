package pl.edu.agh.io_project.integrations.github;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.edu.agh.io_project.projects.Project;
import pl.edu.agh.io_project.projects.ProjectRepository;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class GitHubController {


    private final ProjectRepository projectRepository;
    private final GitHubIntegrationRepository gitHubIntegrationRepository;

    @PostMapping("/connect")
    public ResponseEntity<Void> connectToRepo(@RequestBody GitHubIntegrationRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        GitHubIntegration integration = GitHubIntegration.builder()
                .repoName(request.getRepoName())
                .repoOwner(request.getRepoOwner())
                .installationId(request.getInstallationId())
                .project(project)
                .build();

        gitHubIntegrationRepository.save(integration);
        return ResponseEntity.ok().build();
    }

}
