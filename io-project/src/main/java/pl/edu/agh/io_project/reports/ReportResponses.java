package pl.edu.agh.io_project.reports;

public class ReportResponses {
    public record ReportResultStatus(
            Long reportId,
            Long resultId,
            ReportStatus status
    ) {
    }
}
