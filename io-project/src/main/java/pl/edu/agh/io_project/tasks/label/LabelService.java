package pl.edu.agh.io_project.tasks.label;

import java.util.List;

public interface LabelService {
    List<Label> getAllLabels(String query);
    List<Label> getLabelsByTask(Integer taskId);
    Label getLabelById(Integer labelId);
    Label addLabel(LabelRequest request);
    void deleteLabel(Integer labelId);
    Label updateLabel(Integer labelId, LabelRequest request);
}
