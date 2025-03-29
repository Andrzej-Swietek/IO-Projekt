package pl.edu.agh.io_project.users;

public interface UserService {
    String getUserDetails(String userId);

    String getUserRoles(String userId);
}
