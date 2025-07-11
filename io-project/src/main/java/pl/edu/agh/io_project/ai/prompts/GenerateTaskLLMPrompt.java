package pl.edu.agh.io_project.ai.prompts;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;

@Slf4j
public record GenerateTaskLLMPrompt(
        String taskDescription
) implements LLMPrompt {

    private static final Resource generateTasksSystemMessageResource = new ClassPathResource("prompts/generateTasksSystemMessage.st");
    private static final Resource generateTaskPromptResource = new ClassPathResource("prompts/generateTaskPrompt.st");

    @Override
    public PromptTemplate getPromptTemplate() {
        return new PromptTemplate(generateTaskPromptResource);
    }

    @Override
    public Prompt getPrompt() {
        Map<String, Object> params = Map.of(
                "taskDescription", taskDescription
        );

        var systemMessage = new SystemMessage(generateTasksSystemMessageResource);
        var promptTemplate = new PromptTemplate(generateTaskPromptResource);
        String userMessageContent = promptTemplate.render(params);
        log.info("GEN PROMPT >>>\n{}", userMessageContent);

        return new Prompt(List.of(systemMessage, new UserMessage(userMessageContent)));
    }
}
