package pl.edu.agh.io_project.boards;

public record BoardRequest(
        String name,
        String description,
        String ownerId,
        Long projectId
) {
}
