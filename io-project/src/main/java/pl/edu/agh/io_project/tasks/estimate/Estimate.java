package pl.edu.agh.io_project.tasks.estimate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.edu.agh.io_project.tasks.Task;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Estimate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    private Integer estimatedTime;  // in hours

    @Transient
    private Integer actualTime;     //  in hours | null if not finished

    private LocalDateTime completedAt;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

    @PostLoad
    public void calculateActualTime() {
        if (completedAt != null && createdAt != null) {
            this.actualTime = (int) ChronoUnit.HOURS.between(createdAt, completedAt);
        } else {
            this.actualTime = null;
        }
    }
}