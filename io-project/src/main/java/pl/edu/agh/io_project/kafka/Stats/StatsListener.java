package pl.edu.agh.io_project.kafka.Stats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;


    @KafkaListener(topics = "user-stats", groupId = "stats-consumer-group")
    public void listenUserStats(UserStatsEvent userStatsEvent) {
        log.info("Received UserStatsEvent: {}", userStatsEvent);
        statsService.saveUserStats(userStatsEvent.toEntity());

    }

    @KafkaListener(topics = "team-stats", groupId = "stats-consumer-group")
    public void listenTeamStats(String message) {

        try {
            TeamStatsEvent event = objectMapper.readValue(message, TeamStatsEvent.class);
            log.info("Received TeamStatsEvent: {}", event);
            statsService.saveTeamStats(event.toEntity());
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}