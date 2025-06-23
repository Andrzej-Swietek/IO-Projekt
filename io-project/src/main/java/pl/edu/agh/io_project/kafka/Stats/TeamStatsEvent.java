package pl.edu.agh.io_project.kafka.Stats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.edu.agh.io_project.stats.entities.TeamStatsEntity;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

public record TeamStatsEvent(
        String teamId,
        String projectId,
        Set<String> members,
        Map<String, TaskStats> memberStats,
        Map<String, Integer> dailyTaskIncome,
        Set<String> activeUsers,
        String updatedAt,
        int createdCount,
        int assignedCount,
        int closedCount,
        int movedCount,
        Map<String, Integer> statusCounts,
        double avgCloseTimeSeconds
) {
    public TeamStatsEntity toEntity() {
        return TeamStatsEntity.builder()
                .teamId(teamId)
                .projectId(projectId)
                .members(members)
                .memberStatsJson(memberStatsToJson())
                .dailyTaskIncome(dailyTaskIncome)
                .activeUsers(activeUsers)
                .updatedAt(Instant.parse(updatedAt))
                .createdCount(createdCount)
                .assignedCount(assignedCount)
                .closedCount(closedCount)
                .movedCount(movedCount)
                .statusCounts(statusCounts)
                .avgCloseTimeSeconds(avgCloseTimeSeconds)
                .build();
    }

    private String memberStatsToJson() {
        try {
            return new ObjectMapper().writeValueAsString(memberStats);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize memberStats", e);
        }
    }
}