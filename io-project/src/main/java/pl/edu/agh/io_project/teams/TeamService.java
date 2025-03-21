package pl.edu.agh.io_project.teams;

import java.util.List;

public interface TeamService {
    Team createTeam(Team team);

    Team getTeamById(Long id);

    List<Team> getTeamsByUserId(String userId);

    Team updateTeam(Long id, Team team);

    void deleteTeam(Long id);
}