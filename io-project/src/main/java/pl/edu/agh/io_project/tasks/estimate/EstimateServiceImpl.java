package pl.edu.agh.io_project.tasks.estimate;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EstimateServiceImpl implements EstimateService {

    private final EstimateRepository estimateRepository;

    @Override
    public Estimate createEstimate(Estimate estimate) {
        return estimateRepository.save(estimate);
    }

    @Override
    public Estimate getEstimateById(Long id) {
        return estimateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estimate not found with id: " + id));
    }

    @Override
    public Estimate updateEstimate(Long id, Estimate updatedEstimate) {
        Estimate existing = getEstimateById(id);
        existing.setEstimatedTime(updatedEstimate.getEstimatedTime());
        existing.setCompletedAt(updatedEstimate.getCompletedAt());
        existing.setTask(updatedEstimate.getTask());
        return estimateRepository.save(existing);
    }

    @Override
    public void deleteEstimate(Long id) {
        Estimate existing = getEstimateById(id);
        estimateRepository.delete(existing);
    }

    @Override
    public List<Estimate> getAllEstimates() {
        return estimateRepository.findAll();
    }

    @Override
    public List<Estimate> getEstimatesByTaskId(Long taskId) {
        return estimateRepository.findByTaskId(taskId)
                .map(List::of)
                .orElse(List.of());
    }


    @Override
    public List<Estimate> getEstimatesByUserId(String userId) {
        return estimateRepository.findByUserId(userId);
    }
}