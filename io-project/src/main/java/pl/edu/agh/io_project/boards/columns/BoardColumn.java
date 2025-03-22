package pl.edu.agh.io_project.boards.columns;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.io_project.boards.Board;
import pl.edu.agh.io_project.tasks.Task;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
public class BoardColumn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer position;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @OneToMany(mappedBy = "column", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;
}
