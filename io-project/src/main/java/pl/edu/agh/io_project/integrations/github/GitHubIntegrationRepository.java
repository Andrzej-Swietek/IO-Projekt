package pl.edu.agh.io_project.integrations.github;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GitHubIntegrationRepository extends JpaRepository<GitHubIntegration, Long> {
    Optional<GitHubIntegration> findByProjectId(Long projectId);

    List<GitHubIntegration> findByProjectIdIn(List<Long> projectIds);

    void deleteByProjectId(Long projectId);
}
