package pl.edu.agh.io_project.stats.user;


import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.io_project.stats.entities.UserStatsEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserStatsRepository extends JpaRepository<UserStatsEntity, Long> {
    Optional<UserStatsEntity> findTopByUserIdOrderByLastActiveDesc(String userId);

    List<UserStatsEntity> findByUserId(String userId);

    List<UserStatsEntity> findByAssignmentsGreaterThan(Integer assignments);

    List<UserStatsEntity> findByClosedGreaterThan(Integer closed);

    List<UserStatsEntity> findByLastActiveBefore(Instant date);
}