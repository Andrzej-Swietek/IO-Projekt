package pl.edu.agh.io_project.stats;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.io_project.stats.entities.TeamStatsEntity;
import pl.edu.agh.io_project.stats.entities.UserStatsEntity;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/stats")
@PreAuthorize("isAuthenticated()")
public class StatsController {
    private final StatsService statsService;


    @PostMapping("/user")
    public ResponseEntity<UserStatsEntity> saveUserStats(@RequestBody UserStatsEntity userStats) {
        return ResponseEntity.ok(statsService.saveUserStats(userStats));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserStatsEntity> getUserStatsById(@PathVariable String id) {
        return ResponseEntity.ok(statsService.getUserStatsById(id)
                .orElse(UserStatsEntity.empty(id)));
    }

    @GetMapping("/user/by-user/{userId}")
    public ResponseEntity<List<UserStatsEntity>> getUserStatsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(statsService.getUserStatsByUserId(userId));
    }

    @DeleteMapping("/user/{id}")
    public void deleteUserStats(@PathVariable Long id) {
        statsService.deleteUserStats(id);
    }

    @GetMapping("/user/last-active-before")
    public ResponseEntity<List<UserStatsEntity>> findUserStatsLastActiveBefore(@RequestParam Instant date) {
        return ResponseEntity.ok(statsService.findUserStatsLastActiveBefore(date));
    }

    // --- Team Stats ---

    @PostMapping("/team")
    public ResponseEntity<TeamStatsEntity> saveTeamStats(@RequestBody TeamStatsEntity teamStats) {
        return ResponseEntity.ok(statsService.saveTeamStats(teamStats));
    }

    @GetMapping("/team/{id}")
    public ResponseEntity<TeamStatsEntity> getTeamStatsById(@PathVariable Long id) {
        return ResponseEntity.ok(statsService.getTeamStatsById(id)
                .orElse(TeamStatsEntity.empty("")));
    }

    @GetMapping("/team/by-team/{teamId}")
    public ResponseEntity<TeamStatsEntity> getTeamStatsByTeamId(@PathVariable String teamId) {
        return ResponseEntity.ok(statsService.getTeamStatsByTeamId(teamId).orElse(TeamStatsEntity.empty(teamId)));
    }

    @DeleteMapping("/team/{id}")
    public void deleteTeamStats(@PathVariable Long id) {
        statsService.deleteTeamStats(id);
    }

    @GetMapping("/team/updated-after")
    public ResponseEntity<List<TeamStatsEntity>> findTeamStatsUpdatedAfter(@RequestParam Instant date) {
        return ResponseEntity.ok(statsService.findTeamStatsUpdatedAfter(date));
    }
}
