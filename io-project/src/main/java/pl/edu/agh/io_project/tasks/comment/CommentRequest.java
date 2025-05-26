package pl.edu.agh.io_project.tasks.comment;

public record CommentRequest(
    String authorId,
    String content,
    Long taskId
) {

}
