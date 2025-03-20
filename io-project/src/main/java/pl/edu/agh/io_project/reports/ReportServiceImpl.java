package pl.edu.agh.io_project.reports;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReportResultRepository reportResultRepository;

    @Override
    public Page<Report> getAllReports(Pageable pageable) {
        return reportRepository.findAll(pageable);
    }

    @Transactional
    @Override
    public synchronized Report createReport(ReportRequest reportRequest) {
        Report newReport = Report.builder()
                .title(reportRequest.title())
                .description(reportRequest.description())
                .reportType(reportRequest.reportType())
                .createdByUserId(reportRequest.createdByUserId())
                .build();
        return reportRepository.save(newReport);
    }

    @Override
    public Optional<Report> getReportById(Long id) {
        return reportRepository.findById(id);
    }

    @Override
    public List<Report> getReportsByUserId(String userId) {
        return reportRepository.findByCreatedByUserId(userId);
    }

    @Async
    @Transactional
    public CompletableFuture<ReportResult> generate(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        ReportResult reportResult = new ReportResult();
        reportResult.setReport(report);
        reportResult.setStatus(ReportStatus.IN_PROGRESS);
        reportResult = reportResultRepository.save(reportResult);

        // TODO: GENERATE DOCUMENT

        reportResult.setStatus(ReportStatus.COMPLETED);
        reportResult = reportResultRepository.save(reportResult);

        return CompletableFuture.completedFuture(reportResult);
    }

    @Override
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }
}