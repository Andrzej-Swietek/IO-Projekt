package pl.edu.agh.io_project.tasks.taskHistory;

import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.edu.agh.io_project.tasks.Task;

import java.time.LocalDateTime;

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
    private String action;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}
