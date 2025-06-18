package pl.edu.agh.io_project.tasks.taskHistory;

public enum TaskHistoryAction {
    CREATED,

    UPDATED_TITLE,

    UPDATED_DESCRIPTION,

    UPDATED_STATUS,

    UPDATED_POSITION,

    UPDATED_COLUMN,

    ADDED_ASSIGNEE,
    DELETED_ASSIGNEE,

    ADDED_LABEL,
    DELETED_LABEL,

    ADDED_COMMENT,
    EDITED_COMMENT,
    DELETED_COMMENT,

    CLOSED,

    TASK_DELETED
}

