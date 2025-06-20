package pl.edu.agh.io_project.tasks.estimate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EstimateRepository extends JpaRepository<Estimate, Long> {
    @Query("SELECT AVG(e.estimatedTime) FROM Estimate e")
    Double findAverageEstimatedTime();

    @Query("SELECT e FROM Estimate e WHERE e.task.id = :taskId")
    Optional<Estimate> findByTaskId(@Param("taskId") Long taskId);

    @Query("SELECT e FROM Estimate e WHERE :userId MEMBER OF e.task.assignees")
    List<Estimate> findByUserId(@Param("userId") String userId);
}
