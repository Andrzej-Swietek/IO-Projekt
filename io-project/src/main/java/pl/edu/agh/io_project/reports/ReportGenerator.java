package pl.edu.agh.io_project.reports;

import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.edu.agh.io_project.storage.Storage;

@AllArgsConstructor
@Component
public class ReportGenerator {

    private final ReportResultRepository reportResultRepository;
    private final Storage storage;

    @Async
    @Transactional
    public void generateReportAsync(Long resultId) {
        ReportResult result = reportResultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found"));

        try {
            result.setStatus(ReportStatus.IN_PROGRESS);
            reportResultRepository.save(result);

                // TODO ...

            result.setStatus(ReportStatus.COMPLETED);
            reportResultRepository.save(result);
        } catch (Exception e) {
            result.setStatus(ReportStatus.FAILED);
            reportResultRepository.save(result);
        }
    }

}
