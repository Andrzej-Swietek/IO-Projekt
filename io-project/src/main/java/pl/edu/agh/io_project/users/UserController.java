package pl.edu.agh.io_project.users;

import lombok.AllArgsConstructor;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PostMapping("/populate")
    public ResponseEntity<List<UserRepresentation>> populateUsers(@RequestBody PopulateUsersRequest request) {

        return ResponseEntity.ok(
                request.userIds().stream()
                        .map(keycloakUserService::getUserDetails)
                        .toList()
        );
    }

    @GetMapping("/{userId}/roles")
    public String getUserRoles(@PathVariable String userId) {
        return keycloakUserService.getUserRoles(userId);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(Map.of(
                "User ID", user.getUserId(),
                "User Name", user.getFirstName(),
                "Last Name", user.getLastName(),
                "Email", user.getEmail()
        ));
    }

    @GetMapping("/me-jwt")
    public ResponseEntity<?> meJwt(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(Map.of("userId", userId));
    }
}
