package pl.edu.agh.io_project.users;

import org.keycloak.representations.idm.UserRepresentation;

public interface UserService {
    UserRepresentation getUserDetails(String userId);

    String getUserRoles(String userId);
}
