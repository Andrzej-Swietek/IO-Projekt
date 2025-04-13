package pl.edu.agh.io_project.integrations.webhooks;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/github/webhook")
public class GitHubWebhookController {

    @PostMapping
    public ResponseEntity<Void> handleWebhook(@RequestHeader("X-GitHub-Event") String event,
                                              @RequestBody JsonNode payload) {
        if ("pull_request".equals(event)) {
            boolean merged = payload.path("pull_request").path("merged").asBoolean();
            String title = payload.path("pull_request").path("title").asText();
            if (merged && title.contains("#")) {
//                int taskId = extractTaskIdFromTitle(title);
                // znajdź i zamknij taska o tym ID
            }
        }
        return ResponseEntity.ok().build();
    }
}