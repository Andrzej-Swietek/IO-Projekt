package pl.edu.agh.io_project.reports;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.io_project.config.annotations.QueryBuilder;
import pl.edu.agh.io_project.config.annotations.QueryBuilderParams;
import pl.edu.agh.io_project.reponses.PaginatedResponse;

@RestController
@RequestMapping("/api/v1/report")
@AllArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Report> createReport(@RequestBody Report report) {
        return ResponseEntity.ok(reportService.createReport(report));
    }

    @GetMapping("/all")
    public PaginatedResponse<Report> getAllReports(
            @QueryBuilder QueryBuilderParams query
    ) {
        Page<Report> reportPage = reportService.getAllReports(query.getPageRequest());
        return PaginatedResponse.<Report>builder()
                .data(reportPage.getContent())
                .currentPage(reportPage.getNumber())
                .size(reportPage.getSize())
                .totalCount(reportPage.getTotalElements())
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable Long id) {
        return reportService.getReportById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
