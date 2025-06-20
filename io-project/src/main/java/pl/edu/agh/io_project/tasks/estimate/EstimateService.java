package pl.edu.agh.io_project.tasks.estimate;

import java.util.List;

public interface EstimateService {
    Estimate createEstimate(Estimate estimate);

    Estimate getEstimateById(Long id);

    Estimate updateEstimate(Long id, Estimate estimate);

    void deleteEstimate(Long id);

    List<Estimate> getAllEstimates();

    List<Estimate> getEstimatesByTaskId(Long taskId);

    List<Estimate> getEstimatesByUserId(String userId);
}
