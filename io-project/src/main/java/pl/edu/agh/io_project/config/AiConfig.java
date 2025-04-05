package pl.edu.agh.io_project.config;


import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class AiConfig {

    private ChatClient chatClient;

    @Bean
    public ChatClient chatClient() {
        return chatClient;
    }
}
