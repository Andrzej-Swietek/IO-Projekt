package pl.edu.agh.io_project.integrations.github;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.io_project.integrations.VersionControlIntegration;
import pl.edu.agh.io_project.projects.Project;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
public class GitHubIntegration implements VersionControlIntegration {
    @Id
    @GeneratedValue
    private Long id;

//    @OneToOne(mappedBy = "githubIntegration")
//    private Project project;
    @OneToOne
    @JoinColumn(name = "project_id")
    private Project project;


    private String repoOwner;
    private String repoName;

    private String accessTokenEncrypted;

    private Long installationId;

    private boolean enabled;
}