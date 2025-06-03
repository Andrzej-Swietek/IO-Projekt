package pl.edu.agh.io_project.tasks.label;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.io_project.tasks.TaskRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final TaskRepository taskRepository;

    @Override
    public List<Label> getAllLabels(String query) {
        if (query == null || query.strip().isBlank()) {
            return this.labelRepository.findAll();
        }
        String strippedQuery = query.strip();
        return this.labelRepository.searchByName(strippedQuery);
    }

    @Override
    @Transactional
    public Label getLabelById(Integer labelId) {
        return this.labelRepository.findById(labelId.longValue())
                .orElseThrow(()-> new IllegalStateException("Label not found"));
    }

    @Override
    @Transactional
    public List<Label> getLabelsByTask(Integer taskId) {
        return this.labelRepository.findByTaskId(taskId.longValue());
    }

    @Override
    public Label addLabel(LabelRequest request) {
        var task = this.taskRepository.findById(request.taskId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        Label label = Label.builder()
                .task(task)
                .name(request.name())
                .color(request.color())
                .build();

        return this.labelRepository.save(label);
    }

    @Override
    public void deleteLabel(Integer labelId) {
        this.labelRepository.deleteById(labelId.longValue());
    }

    @Override
    @Transactional
    public Label updateLabel(Integer labelId, LabelRequest request) {
        Label label = this.labelRepository.findById(labelId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("Label not found"));

        var task = taskRepository.findById(request.taskId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        label.setName(request.name());
        label.setColor(request.color());
        label.setTask(task);

        return labelRepository.save(label);
    }
}
