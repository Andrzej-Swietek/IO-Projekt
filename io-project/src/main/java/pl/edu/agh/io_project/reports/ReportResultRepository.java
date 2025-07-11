package pl.edu.agh.io_project.reports;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportResultRepository extends JpaRepository<ReportResult, Long> {
    List<ReportResult> findByReportId(Long reportId);
    Optional<ReportResult> findTopByReportIdOrderByCreatedDateDesc(Long reportId);
}