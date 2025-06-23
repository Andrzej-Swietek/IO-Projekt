package pl.edu.agh.io_project.stats.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public final class UserStatsEntity implements StatsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private Integer assignments;
    private Integer closed;
    private Instant lastActive;

    @ElementCollection
    @CollectionTable(name = "user_status_counts")
    @MapKeyColumn(name = "status")
    @Column(name = "count")
    private Map<String, Integer> statusCounts;

    private Double avgCloseTimeSeconds;

    public static UserStatsEntity empty(String userId) {
        return UserStatsEntity.builder()
                .userId(userId)
                .assignments(0)
                .closed(0)
                .lastActive(Instant.EPOCH)
                .avgCloseTimeSeconds(0.0)
                .build();
    }
}