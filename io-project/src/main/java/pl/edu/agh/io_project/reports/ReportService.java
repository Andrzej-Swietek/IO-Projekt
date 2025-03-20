package pl.edu.agh.io_project.reports;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ReportService {
    Page<Report> getAllReports(Pageable pageable);
    Report createReport(ReportRequest report);
    Optional<Report> getReportById(Long id);
    List<Report> getReportsByUserId(String userId);
    public CompletableFuture<ReportResult> generate(Long reportId);
    void deleteReport(Long id);
}