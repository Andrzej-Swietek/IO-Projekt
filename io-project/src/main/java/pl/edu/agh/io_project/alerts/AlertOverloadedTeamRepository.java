package pl.edu.agh.io_project.alerts;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.io_project.alerts.entities.AlertOverloadedTeamEntity;

import java.util.List;

public interface AlertOverloadedTeamRepository extends JpaRepository<AlertOverloadedTeamEntity, String> {
    List<AlertOverloadedTeamEntity> findByTeamId(String teamId);

    List<AlertOverloadedTeamEntity> findByProjectId(String projectId);
}
