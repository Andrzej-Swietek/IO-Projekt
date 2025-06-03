package pl.edu.agh.io_project.tasks.label;

import java.util.List;
import java.util.Optional;

public interface LabelService {
    List<Label> getAllLabels(Optional<String> query);
    List<Label> getLabelsByTask(Integer taskId);
    Label getLabelById(Integer labelId);
    Label addLabel(LabelRequest request);
    void deleteLabel(Integer labelId);
    Label updateLabel(Integer labelId, LabelRequest request);
}
