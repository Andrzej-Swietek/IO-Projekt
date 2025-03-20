package pl.edu.agh.io_project.reports;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReportResultRepository reportResultRepository;
    private final ReportGenerator generator;

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

    @Transactional
    public ReportResult initializeReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        ReportResult result = ReportResult.builder()
                .status(ReportStatus.IN_PROGRESS)
                .report(report)
                .build();

        reportResultRepository.save(result);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        generator.generateReportAsync(result.getId());
                    }
                });

        return result;
    }

    @Transactional
    public ReportStatus checkStatus(Long reportResultId) {
        ReportResult result = reportResultRepository.findById(reportResultId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        return result.getStatus();
    }

    @Override
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }
}