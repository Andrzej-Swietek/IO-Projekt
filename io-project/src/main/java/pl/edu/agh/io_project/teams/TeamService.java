package pl.edu.agh.io_project.teams;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface TeamService {

    Page<Team> getAllTeams(PageRequest pageRequest);

    Team createTeam(TeamRequest team);

    Team getTeamById(Long id);

    List<Team> getTeamsByUserId(String userId);

    Team updateTeam(Long id, Team team);

    void deleteTeam(Long id);

    TeamMember addTeamMember(TeamMemberRequest teamMemberRequest);
}