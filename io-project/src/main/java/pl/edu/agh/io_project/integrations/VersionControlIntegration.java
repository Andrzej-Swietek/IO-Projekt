package pl.edu.agh.io_project.integrations;

public interface VersionControlIntegration {
    String getRepoOwner();

    String getRepoName();

    Long getInstallationId();

    String getAccessTokenEncrypted();
}
