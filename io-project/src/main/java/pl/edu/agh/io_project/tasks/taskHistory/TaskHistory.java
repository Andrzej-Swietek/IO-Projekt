package pl.edu.agh.io_project.tasks.taskHistory;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.edu.agh.io_project.tasks.Task;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class TaskHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(name = "user_id")
    private String user;

    @Column(nullable = false)
    private TaskHistoryAction action;

    private String actionDescription;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}
