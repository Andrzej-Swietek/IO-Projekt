package pl.edu.agh.io_project.ai.prompts;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;

public record EstimateTaskLLMPrompt(
        Long taskId,
        String taskDescription
) implements LLMPrompt {

    private static final Resource estimateSystemMessage = new ClassPathResource("prompts/estimateSystemMessage.st");
    private static final Resource estimatePromptResource = new ClassPathResource("prompts/estimatePrompt.st");


    @Override
    public PromptTemplate getPromptTemplate() {
        return new PromptTemplate(estimatePromptResource);
    }

    @Override
    public Prompt getPrompt() {
        Map<String, Object> params = Map.of("taskDescription", taskDescription);

        var systemMessage = new SystemMessage(estimateSystemMessage);
        var userMessage = new PromptTemplate(estimatePromptResource)
                .create(params)
                .getContents();
        return new Prompt(List.of(systemMessage, new UserMessage(userMessage)));
    }
}
