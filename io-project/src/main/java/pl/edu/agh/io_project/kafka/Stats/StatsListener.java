package pl.edu.agh.io_project.kafka.Stats;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.edu.agh.io_project.stats.StatsService;

@Component
@AllArgsConstructor
@Slf4j
public class StatsListener {
    private final StatsService statsService;

    @KafkaListener(topics = "user-stats", groupId = "stats-consumer-group")
    public void listenUserStats(UserStatsEvent userStatsEvent) {
        log.info("Received UserStatsEvent: {}", userStatsEvent);
        statsService.saveUserStats(userStatsEvent.toEntity());

    }

    @KafkaListener(topics = "team-stats", groupId = "stats-consumer-group")
    public void listenTeamStats(TeamStatsEvent teamStatsEvent) {
        log.info("Received TeamStatsEvent: {}", teamStatsEvent);
        statsService.saveTeamStats(teamStatsEvent.toEntity());
    }
}