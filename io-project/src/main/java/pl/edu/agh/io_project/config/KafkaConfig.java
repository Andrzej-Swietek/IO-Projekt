package pl.edu.agh.io_project.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.converter.JsonMessageConverter;

@Configuration
public class KafkaConfig {

    @Bean
    public JsonMessageConverter jsonMessageConverter() {
        return new JsonMessageConverter();
    }

    @Bean
    public NewTopic taskEventsTopic() {
        return TopicBuilder.name("task-events")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic alertEventsTopic() {
        return TopicBuilder.name("alert-events")
                .partitions(1)
                .replicas(1)
                .build();
    }

}
