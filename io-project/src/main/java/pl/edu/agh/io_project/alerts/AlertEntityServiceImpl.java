package pl.edu.agh.io_project.alerts;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.agh.io_project.alerts.entities.AlertOverloadedTeamEntity;
import pl.edu.agh.io_project.alerts.entities.AlertTaskStuckEntity;
import pl.edu.agh.io_project.alerts.entities.AlertUserInactiveEntity;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertEntityServiceImpl implements AlertEntityService {
    private final AlertUserInactiveRepository userInactiveRepo;
    private final AlertTaskStuckRepository taskStuckRepo;
    private final AlertOverloadedTeamRepository overloadedTeamRepo;

    // User Inactive
    @Override
    public AlertUserInactiveEntity saveUserInactive(AlertUserInactiveEntity alert) {
        return userInactiveRepo.save(alert);
    }

    @Override
    public List<AlertUserInactiveEntity> getUserInactiveByUserId(String userId) {
        return userInactiveRepo.findByUserId(userId);
    }

    @Override
    public List<AlertUserInactiveEntity> getUserInactiveByDaysInactiveGreaterThan(int days) {
        return userInactiveRepo.findByDaysInactiveGreaterThan(days);
    }

    // Task Stuck
    @Override
    public AlertTaskStuckEntity saveTaskStuck(AlertTaskStuckEntity alert) {
        return taskStuckRepo.save(alert);
    }

    @Override
    public List<AlertTaskStuckEntity> getTaskStuckByUserId(String userId) {
        return taskStuckRepo.findByUserId(userId);
    }

    @Override
    public List<AlertTaskStuckEntity> getTaskStuckByDaysStuckGreaterThan(int days) {
        return taskStuckRepo.findByDaysStuckGreaterThan(days);
    }

    // Overloaded Team
    @Override
    public AlertOverloadedTeamEntity saveOverloadedTeam(AlertOverloadedTeamEntity alert) {
        return overloadedTeamRepo.save(alert);
    }

    @Override
    public List<AlertOverloadedTeamEntity> getOverloadedTeamByTeamId(String teamId) {
        return overloadedTeamRepo.findByTeamId(teamId);
    }

    @Override
    public List<AlertOverloadedTeamEntity> getOverloadedTeamByProjectId(String projectId) {
        return overloadedTeamRepo.findByProjectId(projectId);
    }

    @Override
    public List<AlertUserInactiveEntity> getAllUserInactive() {
        return userInactiveRepo.findAll();
    }

    @Override
    public void deleteUserInactiveById(String id) {
        userInactiveRepo.deleteById(id);
    }

    @Override
    public List<AlertTaskStuckEntity> getAllTaskStuck() {
        return taskStuckRepo.findAll();
    }

    @Override
    public void deleteTaskStuckById(String id) {
        taskStuckRepo.deleteById(id);
    }

    @Override
    public List<AlertOverloadedTeamEntity> getAllOverloadedTeam() {
        return overloadedTeamRepo.findAll();
    }

    @Override
    public void deleteOverloadedTeamById(String id) {
        overloadedTeamRepo.deleteById(id);
    }
}