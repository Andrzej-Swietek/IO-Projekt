package pl.edu.agh.io_project.stats;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.agh.io_project.stats.entities.TeamStatsEntity;
import pl.edu.agh.io_project.stats.entities.UserStatsEntity;
import pl.edu.agh.io_project.stats.team.TeamStatsRepository;
import pl.edu.agh.io_project.stats.user.UserStatsRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final UserStatsRepository userStatsRepository;
    private final TeamStatsRepository teamStatsRepository;

    @Override
    public UserStatsEntity saveUserStats(UserStatsEntity userStats) {
        return userStatsRepository.save(userStats);
    }

    @Override
    public Optional<UserStatsEntity> getUserStatsById(String id) {
        var result = userStatsRepository.findByUserId(id);
        return Optional.of(
                !result.isEmpty() ? result.getFirst() : UserStatsEntity.empty(id)
        );
    }

    @Override
    public List<UserStatsEntity> getUserStatsByUserId(String userId) {
        return userStatsRepository.findByUserId(userId);
    }

    @Override
    public void deleteUserStats(Long id) {
        userStatsRepository.deleteById(id);
    }

    // Team stats
    @Override
    public TeamStatsEntity saveTeamStats(TeamStatsEntity teamStats) {
        return teamStatsRepository.save(teamStats);
    }

    @Override
    public Optional<TeamStatsEntity> getTeamStatsById(Long id) {
        return teamStatsRepository.findById(id);
    }

    @Override
    public Optional<TeamStatsEntity> getTeamStatsByTeamId(String teamId) {
        return Optional.of(
                teamStatsRepository.findTopByTeamIdOrderByUpdatedAtDesc(teamId)
                        .orElseGet(() -> TeamStatsEntity.empty(teamId))
        );
    }

    @Override
    public void deleteTeamStats(Long id) {
        teamStatsRepository.deleteById(id);
    }

    @Override
    public List<UserStatsEntity> findUserStatsLastActiveBefore(Instant date) {
        return userStatsRepository.findByLastActiveBefore(date);
    }

    @Override
    public List<TeamStatsEntity> findTeamStatsUpdatedAfter(Instant date) {
        return teamStatsRepository.findByUpdatedAtAfter(date);
    }
}