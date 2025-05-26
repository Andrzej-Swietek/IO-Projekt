package pl.edu.agh.io_project.tasks.comment;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.agh.io_project.tasks.TaskRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;

    @Transactional
    @Override
    public List<Comment> getCommentsByTask(Long taskId) {
        return commentRepository.findByTaskId(taskId);
    }

    @Override
    public Comment addComment(CommentRequest request) {
        var task = taskRepository.findById(request.taskId())
                .orElseThrow(() -> new IllegalArgumentException("Column not found"));

        Comment comment = Comment.builder()
                .task(task)
                .authorId(request.authorId())
                .content(request.content())
                .build();


        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Integer commentId) {
        commentRepository.deleteById(commentId.longValue());
    }
}
