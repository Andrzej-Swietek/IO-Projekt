package pl.edu.agh.io_project.tasks;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.agh.io_project.tasks.estimate.Estimate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByColumnIdOrderByPosition(Long columnId);

    List<Task> findByAssigneesContaining(String userId);

    List<Task> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT t FROM Task t WHERE LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Task> searchByDescription(@Param("keyword") String keyword);

    List<Task> findByAssigneesIsEmpty();

    List<Task> findByColumnIdAndPositionGreaterThan(Long columnId, Integer position);

    @Query("SELECT t FROM Task t WHERE t.createdDate BETWEEN :startDate AND :endDate")
    List<Task> findTasksBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<Task> findByColumnIdOrderByPositionDesc(Long columnId);

    Optional<Task> findTopByOrderByIdDesc();

    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status")
    long countByStatus(@Param("status") String status);

    @Query("SELECT e FROM Estimate e WHERE e.task.id = :taskId")
    Optional<Estimate> findEstimateByTaskId(@Param("taskId") Long taskId);
}
