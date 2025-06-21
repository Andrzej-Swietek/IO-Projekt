package pl.edu.agh.io_project.tasks.events;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.edu.agh.io_project.kafka.TaskEvent.*;
import pl.edu.agh.io_project.tasks.Task;
import pl.edu.agh.io_project.tasks.TaskStatus;
import pl.edu.agh.io_project.tasks.taskHistory.TaskHistory;
import pl.edu.agh.io_project.tasks.taskHistory.TaskHistoryAction;
import pl.edu.agh.io_project.tasks.taskHistory.TaskHistoryRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Component
@RequiredArgsConstructor
public class TaskChangeListener {

    private final TaskHistoryRepository historyRepository;
    private final TaskEventProducer taskEventProducer;

    @EventListener
    public void handleTaskUpdated(TaskUpdatedEvent event) {
        Task before = event.getBefore();
        Task after = event.getAfter();
        String userId = event.getUserId();
        String teamId = after.getColumn().getBoard().getProject().getTeam().getId().toString();

        List<TaskHistory> historyEntries = new ArrayList<>();

        if (before == null && after != null) {
            // Task created
            historyEntries.add(history(after, userId, TaskHistoryAction.CREATED, "Created task"));
            taskEventProducer.sendTaskEvent(new TaskCreated(
                    after.getId().toString(),
                    after.getTitle(),
                    teamId,
                    after.getColumn().getId().toString(),
                    userId,
                    Instant.now()
            ));
        } else if (before != null && after == null) {
            // Task deleted (closed)
            historyEntries.add(history(before, userId, TaskHistoryAction.TASK_DELETED, "Deleted task"));
            taskEventProducer.sendTaskEvent(new TaskClosed(
                    before.getId().toString(),
                    before.getTitle(),
                    teamId,
                    before.getColumn().getId().toString(),
                    userId,
                    Instant.now()
            ));
        } else {
            assert before != null;
            // Title changed
            if (!Objects.equals(before.getTitle(), after.getTitle())) {
                historyEntries.add(history(after, userId, TaskHistoryAction.UPDATED_TITLE, "Changed task title"));
            }
            // Description changed
            if (!Objects.equals(before.getDescription(), after.getDescription())) {
                historyEntries.add(history(after, userId, TaskHistoryAction.UPDATED_DESCRIPTION, "Changed task description"));
            }
            // Status changed
            if (!Objects.equals(before.getStatus(), after.getStatus())) {
                historyEntries.add(history(after, userId, TaskHistoryAction.UPDATED_STATUS,
                        "Changed status: " + before.getStatus() + " -> " + after.getStatus()));
            }
            // Position changed
            if (!Objects.equals(before.getPosition(), after.getPosition())) {
                historyEntries.add(history(after, userId, TaskHistoryAction.UPDATED_POSITION,
                        "Changed position to: " + after.getPosition()));

                if (TaskStatus.DONE.equals(after.getStatus())) {
                    taskEventProducer.sendTaskEvent(new TaskClosed(
                            after.getId().toString(),
                            after.getTitle(),
                            teamId,
                            after.getColumn().getId().toString(),
                            userId,
                            Instant.now()
                    ));
                }
            }
            // Column changed (Task moved)
            if (!Objects.equals(before.getColumn().getId(), after.getColumn().getId())) {
                historyEntries.add(history(after, userId, TaskHistoryAction.UPDATED_COLUMN,
                        "Moved to column: " + after.getColumn().getName()));
                taskEventProducer.sendTaskMoved(new TaskMoved(
                        after.getId().toString(),
                        teamId,
                        before.getColumn().getId().toString(),
                        after.getColumn().getId().toString(),
                        userId,
                        Instant.now()
                ));
            }
            // Assignees added
            for (String added : after.getAssignees()) {
                if (!before.getAssignees().contains(added)) {
                    historyEntries.add(history(after, userId, TaskHistoryAction.ADDED_ASSIGNEE,
                            "Added assignee: " + added));
                    taskEventProducer.sendTaskEvent(new TaskAssigned(
                            after.getId().toString(),
                            added,
                            teamId,
                            after.getColumn().getBoard().getProject().getId().toString(),
                            Instant.now()
                    ));
                }
            }
            // Assignees removed
            for (String removed : before.getAssignees()) {
                if (!after.getAssignees().contains(removed)) {
                    historyEntries.add(history(after, userId, TaskHistoryAction.DELETED_ASSIGNEE,
                            "Deleted assignee: " + removed));
                    // No event for unassignment in your model
                }
            }
            // Labels added/removed (no events for these in your model)
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