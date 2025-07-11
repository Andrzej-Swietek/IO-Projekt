package pl.edu.agh.io_project.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.edu.agh.io_project.kafka.TaskEvent.TaskAssigned;
import pl.edu.agh.io_project.kafka.TaskEvent.TaskClosed;
import pl.edu.agh.io_project.kafka.TaskEvent.TaskCreated;
import pl.edu.agh.io_project.kafka.TaskEvent.TaskMoved;

@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        mapper.registerSubtypes(
                new NamedType(TaskCreated.class, "TaskCreated"),
                new NamedType(TaskAssigned.class, "TaskAssigned"),
                new NamedType(TaskClosed.class, "TaskClosed"),
                new NamedType(TaskMoved.class, "TaskMoved")
        );
        return mapper;
    }
}