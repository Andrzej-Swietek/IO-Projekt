package pl.edu.agh.io_project.projects;

public record ProjectRequest(
        String name,
        String description,
        Long teamId
) {
}
