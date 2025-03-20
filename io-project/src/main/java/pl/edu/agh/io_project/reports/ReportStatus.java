package pl.edu.agh.io_project.reports;

public enum ReportStatus {
    IN_PROGRESS,
    COMPLETED,
    FAILED;

    public boolean isCompleted() {
        return COMPLETED.equals(this);
    }

    public boolean isFailed() {
        return FAILED.equals(this);
    }

    public boolean isPending() {
        return IN_PROGRESS.equals(this);
    }
}
