package pl.edu.agh.io_project.tasks.label;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.io_project.tasks.Task;
import pl.edu.agh.io_project.tasks.TaskRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final TaskRepository taskRepository;

    @Override
    public List<Label> getAllLabels(Optional<String> query) {
        Optional<String> strippedQuery = query
                .map(String::strip)
                .filter(q -> !q.isBlank());

        return strippedQuery.isEmpty() ?
                this.labelRepository.findAll() : this.labelRepository.searchByName(strippedQuery.get());
    }

    @Override
    @Transactional
    public Label getLabelById(Integer labelId) {
        return this.labelRepository.findById(labelId.longValue())
                .orElseThrow(() -> new EntityNotFoundException("Label not found"));
    }

    @Override
    @Transactional
    public List<Label> getLabelsByTask(Integer taskId) {
        return taskRepository.findById(taskId.longValue())
                .orElseThrow(() -> new EntityNotFoundException("Task not found"))
                .getLabels()
                .stream()
                .toList();
    }

    @Override
    public Label addLabel(LabelRequest request) {
        Label label = Label.builder()
                .name(request.name())
                .color(request.color())
                .build();

        return this.labelRepository.save(label);
    }

    @Transactional
    @Override
    public void deleteLabel(Integer labelId) {
        Label label = labelRepository.findById(labelId.longValue())
                .orElseThrow(() -> new EntityNotFoundException("Label not found"));

        List<Task> tasksWithLabel = taskRepository.findByLabelsId(labelId.longValue());
        for (Task task : tasksWithLabel) {
            task.getLabels().remove(label);
        }
        taskRepository.saveAll(tasksWithLabel);

        this.labelRepository.deleteById(labelId.longValue());
    }

    @Override
    @Transactional
    public Label updateLabel(Integer labelId, LabelRequest request) {
        Label label = this.labelRepository.findById(labelId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("Label not found"));

        label.setName(request.name());
        label.setColor(request.color());

        return labelRepository.save(label);
    }
}
