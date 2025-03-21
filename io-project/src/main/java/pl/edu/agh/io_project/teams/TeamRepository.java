package pl.edu.agh.io_project.teams;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByNameContaining(String name);

    @Query("SELECT m FROM TeamMember m WHERE m.team.name = :name")
    List<String> getTeamMembers(String teamName);
}