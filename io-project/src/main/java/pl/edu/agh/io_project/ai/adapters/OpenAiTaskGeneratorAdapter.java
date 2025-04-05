package pl.edu.agh.io_project.ai.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import pl.edu.agh.io_project.ai.ports.AiTaskGeneratorPort;
import pl.edu.agh.io_project.tasks.Task;
import pl.edu.agh.io_project.tasks.TaskRepository;
import pl.edu.agh.io_project.tasks.TaskStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class OpenAiTaskGeneratorAdapter implements AiTaskGeneratorPort {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TaskRepository taskRepository;


    @Override
    public Task generateTask(String taskDescription, Long columnId, int position) {
        var promptTemplate = new PromptTemplate("""
                    Based on the task description: "{taskDescription}", generate a task JSON:
                    {
                        "title": "Generated Task",
                        "description": "Task details",
                        "status": "TODO",
                        "priority": [1-5],
                        "position: positionParam,
                        "columnId": columnId,
                    }
                """);

        Map<String, Object> params = Map.of(
                "taskDescription", taskDescription,
                "position", position
        );
        Prompt prompt = promptTemplate.create(params);
        String response = chatModel.call(prompt).getResult().getOutput().toString();

        try {
            Task task = objectMapper.readValue(response, Task.class);
            task.setStatus(TaskStatus.TODO);
            return task;
        } catch (Exception e) {
            throw new RuntimeException("AI task generation failed", e);
        }
    }

    @Override
    public List<Task> generateMultipleTasks(String projectDescription, Long columnId, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> generateTask(projectDescription + " - Task " + (i + 1), columnId, i))
                .collect(Collectors.toList());

    }

    private int getNextPosition(Long columnId) {
        return taskRepository.countByColumnId(columnId);
    }
}
