package pl.edu.agh.io_project.kafka.TaskEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskEventProducer {

    private final KafkaTemplate<String, TaskEvent> kafkaTemplate;

    public void sendTaskEvent(final TaskEvent taskEvent) {
        log.info("Producing task event: {}", taskEvent);
        Message<TaskEvent> message = MessageBuilder
                .withPayload(taskEvent)
                .setHeader(KafkaHeaders.TOPIC, "task-events")
                .build();
        kafkaTemplate.send(message);
    }
}
