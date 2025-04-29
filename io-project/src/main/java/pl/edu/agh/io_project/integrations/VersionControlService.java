package pl.edu.agh.io_project.integrations;

public interface VersionControlService {
    public void createIssue(VersionControlIntegration integration, String title, String body);

    public void closeIssue(VersionControlIntegration integration, Long issueId);
}
