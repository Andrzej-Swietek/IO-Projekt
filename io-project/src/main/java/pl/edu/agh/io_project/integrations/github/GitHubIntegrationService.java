package pl.edu.agh.io_project.integrations.github;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.agh.io_project.integrations.VersionControlIntegration;
import pl.edu.agh.io_project.integrations.VersionControlService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GitHubIntegrationService implements VersionControlService {

    private final GitHubAuthenticationService authService;
    private final RestTemplate restTemplate = new RestTemplate();


    @Override
    public void createIssue(VersionControlIntegration integration, String title, String body) {
        GitHubIntegration githubIntegration = (GitHubIntegration) integration;
        String token = authService.getInstallationAccessToken(githubIntegration.getInstallationId());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> issue = new HashMap<>();
        issue.put("title", title);
        issue.put("body", body);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(issue, headers);

        String url = String.format(
                "https://api.github.com/repos/%s/%s/issues",
                integration.getRepoOwner(),
                integration.getRepoName()
        );

        restTemplate.postForEntity(url, request, Void.class);
    }

    @Override
    public void closeIssue(VersionControlIntegration integration, Long issueId) {
        GitHubIntegration githubIntegration = (GitHubIntegration) integration;
        String token = authService.getInstallationAccessToken(githubIntegration.getInstallationId());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(org.springframework.http.MediaType.APPLICATION_JSON));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("state", "closed");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        String url = String.format(
                "https://api.github.com/repos/%s/%s/issues/%d",
                githubIntegration.getRepoOwner(),
                githubIntegration.getRepoName(),
                issueId
        );

        restTemplate.exchange(url, HttpMethod.PATCH, request, Void.class);
    }
}