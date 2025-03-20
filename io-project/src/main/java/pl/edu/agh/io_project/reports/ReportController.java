package pl.edu.agh.io_project.reports;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.edu.agh.io_project.config.annotations.QueryBuilder;
import pl.edu.agh.io_project.config.annotations.QueryBuilderParams;
import pl.edu.agh.io_project.reponses.PaginatedResponse;

@RestController
@RequestMapping("/api/v1/report")
@AllArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Report> createReport(@RequestBody ReportRequest report) {
        return ResponseEntity.ok(reportService.createReport(report));
    }

    @GetMapping("/all")
    public PaginatedResponse<Report> getAllReports(@QueryBuilder QueryBuilderParams query) {
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

    @GetMapping("/by-user-id/{id}")
    public ResponseEntity<List<Report>> getReportById(@PathVariable String id) {
        return ResponseEntity.ok(reportService.getReportsByUserId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{reportId}/generate")
    public SseEmitter generateReport(@PathVariable Long reportId) {
        SseEmitter emitter = new SseEmitter();

        CompletableFuture<ReportResult> future = reportService.generate(reportId);

        future.thenAccept(reportResult -> {
            try {
                emitter.send(reportResult);
                emitter.complete();
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }
}
