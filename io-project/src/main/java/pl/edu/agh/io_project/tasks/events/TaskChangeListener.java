package pl.edu.agh.io_project.tasks.events;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.edu.agh.io_project.tasks.Task;
import pl.edu.agh.io_project.tasks.taskHistory.TaskHistory;
import pl.edu.agh.io_project.tasks.taskHistory.TaskHistoryAction;
import pl.edu.agh.io_project.tasks.taskHistory.TaskHistoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TaskChangeListener {

    private final TaskHistoryRepository historyRepository;

    @EventListener
    public void handleTaskUpdated(TaskUpdatedEvent event) {
        Task before = event.getBefore();
        Task after = event.getAfter();
        String userId = event.getUserId();

        List<TaskHistory> historyEntries = new ArrayList<>();

        if (before == null && after != null) {
            historyEntries.add(history(after, userId, TaskHistoryAction.CREATED, "Created task"));
        } else if (before != null && after == null) {
            historyEntries.add(history(before, userId, TaskHistoryAction.TASK_DELETED, "Deleted task"));
        } else {
            assert before != null; // ensures before and after are not null
            if (!Objects.equals(before.getTitle(), after.getTitle())) {
                historyEntries.add(history(after, userId, TaskHistoryAction.UPDATED_TITLE, "Changed task title"));
            }

            if (!Objects.equals(before.getDescription(), after.getDescription())) {
                historyEntries.add(history(after, userId, TaskHistoryAction.UPDATED_DESCRIPTION, "Changed task description"));
            }

            if (!Objects.equals(before.getStatus(), after.getStatus())) {
                historyEntries.add(history(after, userId, TaskHistoryAction.UPDATED_STATUS,
                        "Changed status: " + before.getStatus() + " -> " + after.getStatus()));
            }

            if (!Objects.equals(before.getPosition(), after.getPosition())) {
                historyEntries.add(history(after, userId, TaskHistoryAction.UPDATED_POSITION,
                        "Changed position to: " + after.getPosition()));
            }

            if (!Objects.equals(before.getColumn().getId(), after.getColumn().getId())) {
                historyEntries.add(history(after, userId, TaskHistoryAction.UPDATED_COLUMN,
                        "Moved to column: " + after.getColumn().getName()));
            }

            for (String added : after.getAssignees()) {
                if (!before.getAssignees().contains(added)) {
                    historyEntries.add(history(after, userId, TaskHistoryAction.ADDED_ASSIGNEE,
                            "Added assignee: " + added));
                }
            }

            for (String removed : before.getAssignees()) {
                if (!after.getAssignees().contains(removed)) {
                    historyEntries.add(history(after, userId, TaskHistoryAction.DELETED_ASSIGNEE,
                            "Deleted assignee: " + removed));
                }
            }

            for (var label : after.getLabels()) {
                if (!before.getLabels().contains(label)) {
                    historyEntries.add(history(after, userId, TaskHistoryAction.ADDED_LABEL,
                            "Added label: " + label.getName()));
                }
            }

            for (var label : before.getLabels()) {
                if (!after.getLabels().contains(label)) {
                    historyEntries.add(history(after, userId, TaskHistoryAction.DELETED_LABEL,
                            "Deleted label: " + label.getName()));
                }
            }
        }

        historyRepository.saveAll(historyEntries);
    }


    private TaskHistory history(Task task, String user, TaskHistoryAction action, String desc) {
        return TaskHistory.builder()
                .task(task)
                .user(user)
                .action(action)
                .actionDescription(desc)
                .build();
    }
}