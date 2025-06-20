package pl.edu.agh.io_project.stats;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.io_project.stats.entities.TeamStatsEntity;
import pl.edu.agh.io_project.stats.entities.UserStatsEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/stats")
@PreAuthorize("isAuthenticated()")
public class StatsController {
    private final StatsService statsService;


    @PostMapping("/user")
    public UserStatsEntity saveUserStats(@RequestBody UserStatsEntity userStats) {
        return statsService.saveUserStats(userStats);
    }

    @GetMapping("/user/{id}")
    public Optional<UserStatsEntity> getUserStatsById(@PathVariable String id) {
        return statsService.getUserStatsById(id);
    }

    @GetMapping("/user/by-user/{userId}")
    public List<UserStatsEntity> getUserStatsByUserId(@PathVariable String userId) {
        return statsService.getUserStatsByUserId(userId);
    }

    @DeleteMapping("/user/{id}")
    public void deleteUserStats(@PathVariable Long id) {
        statsService.deleteUserStats(id);
    }

    @GetMapping("/user/last-active-before")
    public List<UserStatsEntity> findUserStatsLastActiveBefore(@RequestParam Instant date) {
        return statsService.findUserStatsLastActiveBefore(date);
    }

    // --- Team Stats ---

    @PostMapping("/team")
    public TeamStatsEntity saveTeamStats(@RequestBody TeamStatsEntity teamStats) {
        return statsService.saveTeamStats(teamStats);
    }

    @GetMapping("/team/{id}")
    public Optional<TeamStatsEntity> getTeamStatsById(@PathVariable Long id) {
        return statsService.getTeamStatsById(id);
    }

    @GetMapping("/team/by-team/{teamId}")
    public Optional<TeamStatsEntity> getTeamStatsByTeamId(@PathVariable String teamId) {
        return statsService.getTeamStatsByTeamId(teamId);
    }

    @DeleteMapping("/team/{id}")
    public void deleteTeamStats(@PathVariable Long id) {
        statsService.deleteTeamStats(id);
    }

    @GetMapping("/team/updated-after")
    public List<TeamStatsEntity> findTeamStatsUpdatedAfter(@RequestParam Instant date) {
        return statsService.findTeamStatsUpdatedAfter(date);
    }
}
