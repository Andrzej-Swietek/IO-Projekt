package pl.edu.agh.io_project.tasks;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.edu.agh.io_project.boards.BoardColumn;
import pl.edu.agh.io_project.tasks.comment.Comment;
import pl.edu.agh.io_project.tasks.label.Label;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Integer position;

    @ManyToOne
    @JoinColumn(name = "column_id", nullable = false)
    private BoardColumn column;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @ElementCollection
    @CollectionTable(
            name = "task_assignees",
            joinColumns = @JoinColumn(name = "task_id")
    )
    @Column(name = "user_id")
    private List<String> assignees; // USER ID FROM KEYCLOAK

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @ManyToMany
    @JoinTable(
            name = "task_label",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private Set<Label> labels = new HashSet<>();

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;
}