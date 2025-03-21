package pl.edu.agh.io_project.projects;


import jakarta.persistence.*;
import lombok.Data;
import pl.edu.agh.io_project.boards.Board;
import pl.edu.agh.io_project.teams.Team;

import java.util.List;

@Entity
@Data
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boards;
}