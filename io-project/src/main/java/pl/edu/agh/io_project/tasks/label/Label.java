package pl.edu.agh.io_project.tasks.label;

import jakarta.persistence.*;
import lombok.Data;
import pl.edu.agh.io_project.tasks.Task;

@Entity
@Data
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String color = "#33a7ff";

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
}
