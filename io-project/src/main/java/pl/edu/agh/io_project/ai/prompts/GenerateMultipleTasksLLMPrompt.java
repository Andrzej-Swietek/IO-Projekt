package pl.edu.agh.io_project.ai.prompts;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;

public record GenerateMultipleTasksLLMPrompt(
        String tasksDescription,
        Integer count
) implements LLMPrompt {

    private static final Resource generateTasksSystemMessageResource = new ClassPathResource("prompts/generateTasksSystemMessage.st");
    private static final Resource generateMultipleTasksPromptResource = new ClassPathResource("prompts/generateMultipleTasksPrompt.st");

    @Override
    public PromptTemplate getPromptTemplate() {
        return new PromptTemplate(generateMultipleTasksPromptResource);
    }

    @Override
    public Prompt getPrompt() {
        Map<String, Object> params = Map.of(
                "tasksDescription", tasksDescription,
                "count", count
        );

        var systemMessage = new SystemMessage(generateTasksSystemMessageResource);
        var userMessage = new PromptTemplate(generateMultipleTasksPromptResource)
                .create(params)
                .getContents();
        return new Prompt(List.of(systemMessage, new UserMessage(userMessage)));
    }
}
