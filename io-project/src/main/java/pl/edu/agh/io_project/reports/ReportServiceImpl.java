package pl.edu.agh.io_project.reports;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    @Override
    public Page<Report> getAllReports(Pageable pageable) {
        return reportRepository.findAll(pageable);
    }

    @Override
    public Report createReport(Report report) {
        return reportRepository.save(report);
    }

    @Override
    public Optional<Report> getReportById(Long id) {
        return reportRepository.findById(id);
    }

    @Override
    public List<Report> getReportsByUserId(String userId) {
        return reportRepository.findByCreatedByUserId(userId);
    }

    @Override
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }
}