package pl.edu.agh.io_project.stats.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public final class TeamStatsEntity implements StatsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String teamId;
    private String projectId;

    @ElementCollection
    private Set<String> members;

    @Column(columnDefinition = "TEXT")
    private String memberStatsJson;

    @ElementCollection
    @CollectionTable(name = "team_daily_task_income")
    @MapKeyColumn(name = "date")
    @Column(name = "count")
    private Map<String, Integer> dailyTaskIncome;

    @ElementCollection
    private Set<String> activeUsers;

    private Instant updatedAt;
    private Integer createdCount;
    private Integer assignedCount;
    private Integer closedCount;
    private Integer movedCount;

    @ElementCollection
    @CollectionTable(name = "team_status_counts")
    @MapKeyColumn(name = "status")
    @Column(name = "count")
    private Map<String, Integer> statusCounts;

    private Double avgCloseTimeSeconds;

    public static TeamStatsEntity empty(String teamId) {
        return TeamStatsEntity.builder()
                .teamId(teamId)
                .projectId("")
                .members(Set.of())
                .memberStatsJson("")
                .dailyTaskIncome(Map.of())
                .activeUsers(Set.of())
                .updatedAt(Instant.EPOCH)
                .createdCount(0)
                .assignedCount(0)
                .closedCount(0)
                .movedCount(0)
                .statusCounts(Map.of())
                .avgCloseTimeSeconds(0.0)
                .build();
    }
}