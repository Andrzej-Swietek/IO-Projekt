package pl.edu.agh.io_project.kafka.TaskEvent;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTemplate<String, String> taskMovedKafkaTemplate;
    private final ObjectMapper objectMapper;


    public void sendTaskEvent(final TaskEvent taskEvent) {
        try {
            log.info("Producing task event: {}", taskEvent);
            String jsonEvent = objectMapper.writeValueAsString(taskEvent);
            kafkaTemplate.send("task-events", taskEvent.getTaskId(), jsonEvent);
        } catch (Exception e) {
            log.error("Failed to send task event", e);
        }
    }

    public void sendTaskMoved(final TaskMoved taskMoved) {
        try {
            String jsonEvent = objectMapper.writeValueAsString(taskMoved);
            log.info("Producing task moved event: {}", taskMoved);
            taskMovedKafkaTemplate.send("task-events", taskMoved.getTaskId(), jsonEvent);
        } catch (Exception e) {
            log.error("Failed to serialize task moved event", e);
        }
    }
}
