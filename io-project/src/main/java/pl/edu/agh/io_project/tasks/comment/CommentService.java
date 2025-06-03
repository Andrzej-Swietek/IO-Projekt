package pl.edu.agh.io_project.tasks.comment;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByTask(Long taskId);
    Comment getCommentById(Integer commentId);
    Comment addComment(CommentRequest request);
    void deleteComment(Integer commentId);
    Comment updateComment(Integer commentId, CommentRequest request);
}
