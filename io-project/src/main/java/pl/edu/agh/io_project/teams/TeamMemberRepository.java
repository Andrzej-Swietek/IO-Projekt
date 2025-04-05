package pl.edu.agh.io_project.teams;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    @Query("SELECT tm FROM TeamMember tm WHERE tm.userId = :userId")
    List<TeamMember> getTeamMemberByUserId(@Param("userId") String userId);

    @Query("SELECT tm FROM TeamMember tm WHERE tm.team.id = :teamId")
    List<TeamMember> getTeamMemberByTeamId(@Param("teamId") String teamId);

    @Query("SELECT tm FROM TeamMember tm WHERE tm.team.id = :teamId AND tm.userId = :userId")
    List<TeamMember> getTeamMemberByTeamIdAndUserId(@Param("teamId") String teamId, @Param("userId") String userId);
}
