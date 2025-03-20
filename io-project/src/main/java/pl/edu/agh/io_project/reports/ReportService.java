package pl.edu.agh.io_project.reports;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReportService {
    Page<Report> getAllReports(Pageable pageable);
    Report createReport(Report report);
    Optional<Report> getReportById(Long id);
    List<Report> getReportsByUserId(String userId);
    void deleteReport(Long id);
}