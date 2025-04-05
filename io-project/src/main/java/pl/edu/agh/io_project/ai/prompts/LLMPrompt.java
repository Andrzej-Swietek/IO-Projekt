package pl.edu.agh.io_project.ai.prompts;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

public interface LLMPrompt {
    PromptTemplate getPromptTemplate();

    Prompt getPrompt();
}
