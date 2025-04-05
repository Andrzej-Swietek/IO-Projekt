package pl.edu.agh.io_project.ai.adapters;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import pl.edu.agh.io_project.ai.EstimateResponse;
import pl.edu.agh.io_project.ai.ports.AiEstimatorPort;
import pl.edu.agh.io_project.ai.prompts.EstimateTaskLLMPrompt;
import pl.edu.agh.io_project.exceptions.AIFailureException;
import pl.edu.agh.io_project.tasks.Task;
import pl.edu.agh.io_project.tasks.TaskRepository;
import pl.edu.agh.io_project.tasks.estimate.Estimate;
import pl.edu.agh.io_project.tasks.estimate.EstimateRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OpenAiEstimatorAdapter implements AiEstimatorPort {

    private final ChatClient chatClient;
    private final EstimateRepository estimateRepository;
    private final TaskRepository taskRepository;

    @Override
    public Estimate estimateTask(Long taskId, String taskDescription) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Task with id " + taskId + " not found"));

        Prompt prompt = new EstimateTaskLLMPrompt(taskId, taskDescription).getPrompt();
        EstimateResponse response = Optional.ofNullable(
                chatClient.prompt(prompt)
                        .call()
                        .entity(EstimateResponse.class)
        ).orElseThrow(() -> new AIFailureException(List.of("AI result Parsing ")));

        try {
            Estimate estimate = Estimate.builder()
                    .estimatedTime(response.estimatedHours())
                    .task(task)
                    .createdAt(LocalDateTime.now())
                    .build();

            estimate.setTask(task);

            return estimateRepository.save(estimate);
        } catch (Exception e) {
            throw new RuntimeException("AI estimation failed", e);
        }
    }
}
