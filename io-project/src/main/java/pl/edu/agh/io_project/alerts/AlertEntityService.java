package pl.edu.agh.io_project.alerts;

import pl.edu.agh.io_project.alerts.entities.AlertOverloadedTeamEntity;
import pl.edu.agh.io_project.alerts.entities.AlertTaskStuckEntity;
import pl.edu.agh.io_project.alerts.entities.AlertUserInactiveEntity;

import java.util.List;

public interface AlertEntityService {
    // User Inactive Alerts
    AlertUserInactiveEntity saveUserInactive(AlertUserInactiveEntity alert);

    List<AlertUserInactiveEntity> getUserInactiveByUserId(String userId);

    List<AlertUserInactiveEntity> getUserInactiveByDaysInactiveGreaterThan(int days);

    List<AlertUserInactiveEntity> getAllUserInactive();

    void deleteUserInactiveById(String id);

    // Task Stuck Alerts
    AlertTaskStuckEntity saveTaskStuck(AlertTaskStuckEntity alert);

    List<AlertTaskStuckEntity> getTaskStuckByUserId(String userId);

    List<AlertTaskStuckEntity> getTaskStuckByDaysStuckGreaterThan(int days);

    List<AlertTaskStuckEntity> getAllTaskStuck();

    void deleteTaskStuckById(String id);

    // Overloaded Team Alerts
    AlertOverloadedTeamEntity saveOverloadedTeam(AlertOverloadedTeamEntity alert);

    List<AlertOverloadedTeamEntity> getOverloadedTeamByTeamId(String teamId);

    List<AlertOverloadedTeamEntity> getOverloadedTeamByProjectId(String projectId);

    List<AlertOverloadedTeamEntity> getAllOverloadedTeam();

    void deleteOverloadedTeamById(String id);
}