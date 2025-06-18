package pl.edu.agh.io_project.tasks.events;

import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pl.edu.agh.io_project.tasks.Task;

@Getter
@Builder
public class TaskUpdatedEvent extends ApplicationEvent {
    private final Task before;
    private final Task after;
    private final String userId;

    public TaskUpdatedEvent(Task before, Task after, String userId) {
        super(Task.class);
        this.before = before;
        this.after = after;
        this.userId = userId;
    }
}
