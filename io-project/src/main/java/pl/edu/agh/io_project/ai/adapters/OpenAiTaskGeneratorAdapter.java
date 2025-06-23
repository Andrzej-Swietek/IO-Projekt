package pl.edu.agh.io_project.ai.adapters;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.io_project.ai.TaskGenerationResponse;
import pl.edu.agh.io_project.ai.ports.AiTaskGeneratorPort;
import pl.edu.agh.io_project.ai.prompts.GenerateMultipleTasksLLMPrompt;
import pl.edu.agh.io_project.ai.prompts.GenerateTaskLLMPrompt;
import pl.edu.agh.io_project.boards.columns.BoardColumn;
import pl.edu.agh.io_project.boards.columns.BoardColumnRepository;
import pl.edu.agh.io_project.exceptions.AIFailureException;
import pl.edu.agh.io_project.tasks.Task;
import pl.edu.agh.io_project.tasks.TaskRepository;
import pl.edu.agh.io_project.tasks.TaskStatus;
import pl.edu.agh.io_project.tasks.estimate.Estimate;
import pl.edu.agh.io_project.tasks.estimate.EstimateRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiTaskGeneratorAdapter implements AiTaskGeneratorPort {

    private final ChatClient chatClient;
    private final EstimateRepository estimateRepository;
    private final TaskRepository taskRepository;
    private final BoardColumnRepository boardColumnRepository;


    @Transactional
    @Override
    public Task generateTask(String taskDescription, Long columnId, int position) {
        BoardColumn boardColumn = boardColumnRepository.findById(columnId)
                .orElseThrow(() -> new EntityNotFoundException("Column with Id " + columnId + "not found"));

        Prompt prompt = new GenerateTaskLLMPrompt(taskDescription).getPrompt();
        log.info(prompt.toString());

        TaskGenerationResponse response = Optional.ofNullable(
                chatClient.prompt(prompt)
                        .call()
                        .entity(TaskGenerationResponse.class)
        ).orElseThrow(() -> new AIFailureException(List.of("AI result Parsing ")));
        log.info("Raw response: {}", response.getTitle());
        log.info("Raw response: {}", response.getDescription());
        log.info("Raw response: {}", response.getEstimatedHours());
        String rawResponse = chatClient.prompt(prompt).call().content();
        log.info("AI raw response: {}", rawResponse);

        try {
            Task task = Task.builder()
                    .title(response.getTitle())
                    .description(response.getDescription())
                    .position(position)
                    .status(TaskStatus.TODO)
                    .comments(List.of())
                    .labels(Set.of())
                    .assignees(List.of())
                    .column(boardColumn)
                    .build();

            taskRepository.save(task);

            Estimate estimate = Estimate.builder()
                    .estimatedTime(response.getEstimatedHours())
                    .task(task)
                    .createdAt(LocalDateTime.now())
                    .build();

            estimateRepository.save(estimate);

            return task;
        } catch (Exception e) {
            throw new RuntimeException("AI task generation failed", e);
        }
    }

    @Override
    public List<Task> generateMultipleTasks(String projectDescription, Long columnId, int count) {
        BoardColumn boardColumn = boardColumnRepository.findById(columnId)
                .orElseThrow(() -> new EntityNotFoundException("Column with Id " + columnId + "not found"));
        int firstPosition = getNextPosition(columnId);

        Prompt prompt = new GenerateMultipleTasksLLMPrompt(projectDescription, count).getPrompt();
        log.info(prompt.toString());

        List<TaskGenerationResponse> responses = Optional.ofNullable(
                chatClient.prompt(prompt)
                        .call()
                        .entity(new ParameterizedTypeReference<List<TaskGenerationResponse>>() {
                        })
        ).orElseThrow(() -> new AIFailureException(List.of("AI result parsing failed")));
        log.info("Raw response: {}", responses);

        return IntStream.range(0, responses.size())
                .mapToObj(i -> {
                    TaskGenerationResponse response = responses.get(i);
                    Task task = Task.builder()
                            .title(response.getTitle())
                            .description(response.getDescription())
                            .position(firstPosition + i)
                            .status(TaskStatus.TODO)
                            .column(boardColumn)
                            .build();
                    taskRepository.save(task);

                    Estimate estimate = Estimate.builder()
                            .estimatedTime(response.getEstimatedHours())
                            .task(task)
                            .createdAt(LocalDateTime.now())
                            .build();
                    estimateRepository.save(estimate);

                    return task;
                }).toList();
    }

    private int getNextPosition(Long columnId) {
        return taskRepository.countByColumnId(columnId) + 1;
    }
}