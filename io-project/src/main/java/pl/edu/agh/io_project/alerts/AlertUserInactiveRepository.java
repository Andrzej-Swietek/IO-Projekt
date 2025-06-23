package pl.edu.agh.io_project.alerts;


import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.io_project.alerts.entities.AlertUserInactiveEntity;

import java.util.List;

public interface AlertUserInactiveRepository extends JpaRepository<AlertUserInactiveEntity, String> {
    List<AlertUserInactiveEntity> findByUserId(String userId);

    List<AlertUserInactiveEntity> findByDaysInactiveGreaterThan(int days);
}