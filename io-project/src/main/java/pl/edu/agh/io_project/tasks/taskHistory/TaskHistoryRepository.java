package pl.edu.agh.io_project.tasks.taskHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {

    List<TaskHistory> findByTaskIdOrderByTimestampDesc(Long taskId);

    void deleteByTaskId(Long taskId);
}
