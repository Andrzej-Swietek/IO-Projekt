package pl.edu.agh.io_project.users;

import java.util.List;

public record PopulateUsersRequest(
        List<String> userIds
) {
}
