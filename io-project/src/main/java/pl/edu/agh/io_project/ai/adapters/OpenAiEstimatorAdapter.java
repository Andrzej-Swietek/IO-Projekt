package pl.edu.agh.io_project.ai.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import pl.edu.agh.io_project.ai.ports.AiEstimatorPort;
import pl.edu.agh.io_project.tasks.Task;
import pl.edu.agh.io_project.tasks.estimate.Estimate;
import pl.edu.agh.io_project.tasks.estimate.EstimateRepository;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiEstimatorAdapter implements AiEstimatorPort {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EstimateRepository estimateRepository;

    @Override
    public Estimate estimateTask(Long taskId, String taskDescription) {
        var promptTemplate = new PromptTemplate("""
                    Given the task: "{taskDescription}", estimate the time required (in hours) as JSON:
                    {
                        "estimatedTime": 8
                    }
                """);

        Map<String, Object> params = Map.of("taskDescription", taskDescription);
        Prompt prompt = promptTemplate.create(params);
        String response = chatModel.call(prompt).getResult().getOutput().toString();

        try {
            Estimate estimate = objectMapper.readValue(response, Estimate.class);
            estimate.setCreatedAt(LocalDateTime.now());

            Task task = new Task();
            task.setId(taskId);
            estimate.setTask(task);

            return estimateRepository.save(estimate);
        } catch (Exception e) {
            throw new RuntimeException("AI estimation failed", e);
        }
    }
}
