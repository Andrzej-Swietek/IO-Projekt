package pl.edu.agh.io_project.users;

import lombok.AllArgsConstructor;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final KeycloakUserService keycloakUserService;

    @GetMapping
    public List<UserRepresentation> getAllUsers() {
        return keycloakUserService.getAllUsers();
    }

    @GetMapping("/roles")
    public List<RoleRepresentation> getAllRoles() {
        return keycloakUserService.getAllRoles();
    }

    @GetMapping("/{userId}")
    public UserRepresentation getUserDetails(@PathVariable String userId) {
        return keycloakUserService.getUserDetails(userId);
    }

    @GetMapping("/{userId}/roles")
    public String getUserRoles(@PathVariable String userId) {
        return keycloakUserService.getUserRoles(userId);
    }
}
