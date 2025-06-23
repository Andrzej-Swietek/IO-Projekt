package pl.edu.agh.io_project.alerts;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.io_project.alerts.entities.AlertTaskStuckEntity;

import java.util.List;

public interface AlertTaskStuckRepository extends JpaRepository<AlertTaskStuckEntity, String> {
    List<AlertTaskStuckEntity> findByUserId(String userId);

    List<AlertTaskStuckEntity> findByDaysStuckGreaterThan(int days);
}