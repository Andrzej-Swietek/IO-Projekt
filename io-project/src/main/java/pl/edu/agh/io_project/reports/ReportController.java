package pl.edu.agh.io_project.reports;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.edu.agh.io_project.config.annotations.QueryBuilder;
import pl.edu.agh.io_project.config.annotations.QueryBuilderParams;
import pl.edu.agh.io_project.responses.PaginatedResponse;

@RestController
@RequestMapping("/api/v1/report")
@AllArgsConstructor
@Slf4j
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
    public ResponseEntity<ReportResponses.ReportResultStatus> generateReport(@PathVariable Long reportId) {
        ReportResult result = reportService.initializeReport(reportId);
        return ResponseEntity.ok(
                new ReportResponses.ReportResultStatus(
                        reportId,
                        result.getId(),
                        result.getStatus()
                ));
    }

    @GetMapping("/status/{resultId}")
    public SseEmitter trackProgress(@PathVariable Long resultId) {
        SseEmitter emitter = new SseEmitter(120_000L);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            try {
                ReportStatus status = reportService.checkStatus(resultId);

                emitter.send(SseEmitter.event()
                        .data(Map.of("status", status))
                        .id(String.valueOf(System.currentTimeMillis())));

                if (status.isCompleted() || status.isFailed()) {
                    emitter.complete();
                    scheduler.shutdown();
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
                scheduler.shutdown();
            }
        }, 0, 1, TimeUnit.SECONDS);

        return emitter;
    }
}
