package pl.edu.agh.io_project.reports;

public record ReportRequest(
        String title,
        String description,
        ReportType reportType,
        String createdByUserId
) {
}
