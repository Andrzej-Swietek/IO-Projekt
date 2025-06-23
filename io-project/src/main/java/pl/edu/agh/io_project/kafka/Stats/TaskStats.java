package pl.edu.agh.io_project.kafka.Stats;

import java.util.Optional;

public record TaskStats(
        int todo,
        int inProgress,
        int done,
        int closedCount,
        Optional<Long> avgTimeToClose, // millis
        double avgLoad
) {
}