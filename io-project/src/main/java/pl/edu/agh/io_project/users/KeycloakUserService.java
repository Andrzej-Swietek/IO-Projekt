package pl.edu.agh.io_project.users;

import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class KeycloakUserService implements UserService {

    private final RestTemplate restTemplate;
    private final KeycloakAuthService authService;
    private final KeycloakService keycloakService;

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    public KeycloakUserService(KeycloakAuthService authService, KeycloakService keycloakService) {
        this.restTemplate = new RestTemplate();
        this.authService = authService;
        this.keycloakService = keycloakService;
    }

    public UserRepresentation getUserDetails(String userId) {
        return this.keycloakService.getUserById(userId);
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

    public List<UserRepresentation> getAllUsers() {
        return this.keycloakService.getAllUsers();
    }

    public List<RoleRepresentation> getAllRoles() {
        return this.keycloakService.getAllRoles();
    }
}
