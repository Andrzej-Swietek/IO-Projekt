package pl.edu.agh.io_project.reports;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByCreatedByUserId(String userId);
}