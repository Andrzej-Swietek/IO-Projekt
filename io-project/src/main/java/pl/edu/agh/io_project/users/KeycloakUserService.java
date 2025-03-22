package pl.edu.agh.io_project.users;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KeycloakUserService implements UserService {

    private final RestTemplate restTemplate;
    private final KeycloakAuthService authService;

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    public KeycloakUserService(KeycloakAuthService authService) {
        this.restTemplate = new RestTemplate();
        this.authService = authService;
    }

    public String getUserDetails(String userId) {
        String token = authService.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = String.format("%s/admin/realms/%s/users/%s", serverUrl, realm, userId);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }

    public String getUserRoles(String userId) {
        String token = authService.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = String.format("%s/admin/realms/%s/users/%s/role-mappings/realm", serverUrl, realm, userId);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }
}
