package pl.edu.agh.io_project.boards.columns;

public record BoardColumnRequest(
        String name,
        Integer position,
        Long boardId
) {
}
