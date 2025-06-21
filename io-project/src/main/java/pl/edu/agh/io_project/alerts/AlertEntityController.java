package pl.edu.agh.io_project.alerts;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.io_project.alerts.entities.AlertOverloadedTeamEntity;
import pl.edu.agh.io_project.alerts.entities.AlertTaskStuckEntity;
import pl.edu.agh.io_project.alerts.entities.AlertUserInactiveEntity;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertEntityController {
    private final AlertEntityService alertService;

    // --- User Inactive Alerts ---
    @PostMapping("/user-inactive")
    public ResponseEntity<AlertUserInactiveEntity> addUserInactive(@RequestBody AlertUserInactiveEntity alert) {
        return ResponseEntity.ok(alertService.saveUserInactive(alert));
    }

    @GetMapping("/user-inactive")
    public List<AlertUserInactiveEntity> getAllUserInactive() {
        return alertService.getAllUserInactive();
    }

    @GetMapping("/user-inactive/user/{userId}")
    public List<AlertUserInactiveEntity> getUserInactiveByUserId(@PathVariable String userId) {
        return alertService.getUserInactiveByUserId(userId);
    }

    @GetMapping("/user-inactive/days/{days}")
    public List<AlertUserInactiveEntity> getUserInactiveByDays(@PathVariable int days) {
        return alertService.getUserInactiveByDaysInactiveGreaterThan(days);
    }

    @DeleteMapping("/user-inactive/{id}")
    public ResponseEntity<Void> deleteUserInactive(@PathVariable String id) {
        alertService.deleteUserInactiveById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Task Stuck Alerts ---
    @PostMapping("/task-stuck")
    public ResponseEntity<AlertTaskStuckEntity> addTaskStuck(@RequestBody AlertTaskStuckEntity alert) {
        return ResponseEntity.ok(alertService.saveTaskStuck(alert));
    }

    @GetMapping("/task-stuck")
    public List<AlertTaskStuckEntity> getAllTaskStuck() {
        return alertService.getAllTaskStuck();
    }

    @GetMapping("/task-stuck/user/{userId}")
    public List<AlertTaskStuckEntity> getTaskStuckByUserId(@PathVariable String userId) {
        return alertService.getTaskStuckByUserId(userId);
    }

    @GetMapping("/task-stuck/days/{days}")
    public List<AlertTaskStuckEntity> getTaskStuckByDays(@PathVariable int days) {
        return alertService.getTaskStuckByDaysStuckGreaterThan(days);
    }

    @DeleteMapping("/task-stuck/{id}")
    public ResponseEntity<Void> deleteTaskStuck(@PathVariable String id) {
        alertService.deleteTaskStuckById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Overloaded Team Alerts ---
    @PostMapping("/overloaded-team")
    public ResponseEntity<AlertOverloadedTeamEntity> addOverloadedTeam(@RequestBody AlertOverloadedTeamEntity alert) {
        return ResponseEntity.ok(alertService.saveOverloadedTeam(alert));
    }

    @GetMapping("/overloaded-team")
    public List<AlertOverloadedTeamEntity> getAllOverloadedTeam() {
        return alertService.getAllOverloadedTeam();
    }

    @GetMapping("/overloaded-team/team/{teamId}")
    public List<AlertOverloadedTeamEntity> getOverloadedTeamByTeamId(@PathVariable String teamId) {
        return alertService.getOverloadedTeamByTeamId(teamId);
    }

    @GetMapping("/overloaded-team/project/{projectId}")
    public List<AlertOverloadedTeamEntity> getOverloadedTeamByProjectId(@PathVariable String projectId) {
        return alertService.getOverloadedTeamByProjectId(projectId);
    }

    @DeleteMapping("/overloaded-team/{id}")
    public ResponseEntity<Void> deleteOverloadedTeam(@PathVariable String id) {
        alertService.deleteOverloadedTeamById(id);
        return ResponseEntity.noContent().build();
    }
}