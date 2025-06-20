package pl.edu.agh.io_project.stats.team;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.io_project.stats.entities.TeamStatsEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TeamStatsRepository extends JpaRepository<TeamStatsEntity, Long> {
    Optional<TeamStatsEntity> findTopByTeamIdOrderByUpdatedAtDesc(String teamId);

    List<TeamStatsEntity> findByTeamId(String teamId);

    List<TeamStatsEntity> findByUpdatedAtAfter(Instant date);
}