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

    @Column(columnDefinition = "VARCHAR(9) DEFAULT '##33a7ff'")
    private String color;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
}
