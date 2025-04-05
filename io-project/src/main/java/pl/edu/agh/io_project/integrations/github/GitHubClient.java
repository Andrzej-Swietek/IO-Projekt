package pl.edu.agh.io_project.integrations.github;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GitHubClient {
    private final RestTemplate restTemplate;
    private final GitHubConfig config;

    public GitHubClient(GitHubConfig config) {
        this.restTemplate = new RestTemplate();
        this.config = config;
    }

    public String getIssues(String repoOwner, String repoName) {
        String url = String.format("%s/repos/%s/%s/issues", config.getApiUrl(), repoOwner, repoName);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + config.getToken());
        headers.set("Accept", "application/vnd.github.v3+json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }
}