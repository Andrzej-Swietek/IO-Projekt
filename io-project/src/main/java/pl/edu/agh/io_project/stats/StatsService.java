package pl.edu.agh.io_project.stats;

import pl.edu.agh.io_project.stats.entities.TeamStatsEntity;
import pl.edu.agh.io_project.stats.entities.UserStatsEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

interface UserStatsService {
    UserStatsEntity saveUserStats(UserStatsEntity userStats);

    Optional<UserStatsEntity> getUserStatsById(String id);

    List<UserStatsEntity> getUserStatsByUserId(String userId);

    void deleteUserStats(Long id);
}

interface TeamStatsService {
    TeamStatsEntity saveTeamStats(TeamStatsEntity teamStats);

    Optional<TeamStatsEntity> getTeamStatsById(Long id);

    Optional<TeamStatsEntity> getTeamStatsByTeamId(String teamId);

    void deleteTeamStats(Long id);
}

public interface StatsService extends UserStatsService, TeamStatsService {
    List<UserStatsEntity> findUserStatsLastActiveBefore(Instant date);

    List<TeamStatsEntity> findTeamStatsUpdatedAfter(Instant date);
}
