package pl.edu.agh.io_project.reports;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class ReportResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Column(columnDefinition = "TEXT")
    private String details;

    private Boolean success;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdDate;
}