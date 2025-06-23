package pl.edu.agh.io_project.kafka.Stats;

import pl.edu.agh.io_project.stats.entities.UserStatsEntity;

import java.time.Instant;
import java.util.Map;

public record UserStatsEvent(
        String userId,
        int assignments,
        int closed,
        String lastActive,
        Map<String, Integer> statusCounts,
        double avgCloseTimeSeconds
) {
    public UserStatsEntity toEntity() {
        return UserStatsEntity.builder()
                .userId(userId)
                .assignments(assignments)
                .closed(closed)
                .lastActive(Instant.parse(lastActive))
                .statusCounts(statusCounts)
                .avgCloseTimeSeconds(avgCloseTimeSeconds)
                .build();
    }
}
