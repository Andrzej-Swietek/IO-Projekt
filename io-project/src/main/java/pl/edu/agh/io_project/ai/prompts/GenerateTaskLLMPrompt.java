package pl.edu.agh.io_project.ai.prompts;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

public record GenerateTaskLLMPrompt(
        String taskDescription
) implements LLMPrompt {
    @Override
    public PromptTemplate getPromptTemplate() {
        return null;
    }

    @Override
    public Prompt getPrompt() {
        return null;
    }
}
