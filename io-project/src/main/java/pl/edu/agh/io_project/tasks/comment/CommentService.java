package pl.edu.agh.io_project.tasks.comment;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByTask(Long taskId);
    Comment addComment(CommentRequest request);
    void deleteComment(Integer commentId);
}
